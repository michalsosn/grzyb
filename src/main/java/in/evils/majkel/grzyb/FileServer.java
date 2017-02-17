package in.evils.majkel.grzyb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.func.Action;
import ratpack.handling.Chain;
import ratpack.handling.Handlers;
import ratpack.http.client.HttpClient;
import ratpack.server.RatpackServer;
import ratpack.server.ServerConfig;

import java.net.URI;
import java.nio.file.Paths;
import java.time.Duration;

public class FileServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileServer.class);
    private final RatpackServer server;
    private final HttpClient httpClient;

    public FileServer(ServerConfig serverConfig) throws Exception {
        this.server = RatpackServer.of(spec -> spec
                .serverConfig(serverConfig)
                .handlers(createHandlers())
        );
        this.httpClient = HttpClient.of(httpClientSpec -> httpClientSpec
                .readTimeout(Duration.ofSeconds(10))
        );
    }

    public FileServer() throws Exception {
        this(createConfig());
    }

    public void start() throws Exception {
        server.start();
    }

    private static ServerConfig createConfig() throws Exception {
        return ServerConfig.of(spec -> spec
                .baseDir(Paths.get(".").toRealPath())
        );
    }

    private Action<? super Chain> createHandlers() {
        return chain -> chain.all(ctx -> {
            if (ctx.getRequest().getMethod().isGet()) {
                Handlers.files(chain.getServerConfig(), Action.noop()).handle(ctx);
            } else {
                final URI uri = new URI(
                        server.getScheme(), null, server.getBindHost(), server.getBindPort(),
                        "/" + ctx.getRequest().getPath(), ctx.getRequest().getQuery(), null
                );
                LOGGER.info("Redirected {} request to {}", ctx.getRequest().getMethod().getName(), uri);
                httpClient.get(uri).then(response -> {
                    Thread.sleep(1000 * 5);
                    response.forwardTo(ctx.getResponse());
                });
            }
        });
    }
}
