
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Server {

    public static void main(String[] args) throws IOException {
        int copy = 0;
        long total = 0;
        ServerSocketChannel serversocketchannel = ServerSocketChannel.open();
        serversocketchannel.bind(new InetSocketAddress(1111));
        SocketChannel socketchannel = serversocketchannel.accept();

        ServerSocket serversocket = new ServerSocket(2222);
        Socket sock = serversocket.accept();
        DataInputStream inputnakrub = new DataInputStream(sock.getInputStream());
        DataOutputStream outputnakrub = new DataOutputStream(sock.getOutputStream());

        String path = "C:\\server\\";
        File files = new File(path);
        File[] listfile = files.listFiles();

        outputnakrub.writeInt(listfile.length);
        for (int i = 0; i < listfile.length; i++) {
            outputnakrub.writeUTF(listfile[i].getName());
            outputnakrub.writeLong(listfile[i].length() / (1048576));
        }
        int Choose = inputnakrub.readInt() - 1;

        outputnakrub.writeUTF(listfile[Choose].getName());
        outputnakrub.writeLong(listfile[Choose].length());

        Path path2 = Paths.get(listfile[Choose].getPath());
        FileChannel filech = FileChannel.open(path2);
        ByteBuffer bufferkrub = ByteBuffer.allocate(8192);
        while (true) {
            copy = filech.read(bufferkrub);
            bufferkrub.flip();
            socketchannel.write(bufferkrub);
            bufferkrub.clear();
            total += copy;
            if (total == listfile[Choose].length()) {
                break;
            }
        }
        boolean finish = inputnakrub.readBoolean();
    }
}
