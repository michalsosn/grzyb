package in.evils.majkel.grzyb;

import ratpack.server.ServerConfig;

import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws Exception {
        final ServerConfig serverConfig = parseArgs(args);
        final FileServer fileServer = new FileServer(serverConfig);
        fileServer.start();
    }

    private static ServerConfig parseArgs(String[] args) throws Exception {
        final int port = args.length > 0 ? Integer.parseInt(args[0]) : 5050;
        return ServerConfig.of(spec -> spec
                .port(port)
                .baseDir(Paths.get(".").toRealPath())
                .development(true)
        );
    }
}
