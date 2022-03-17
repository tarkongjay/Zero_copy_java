import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws IOException {
        int copyfile = 0;
        long total = 0;
        Scanner scan = new Scanner(System.in);
        SocketChannel socketchannel = SocketChannel.open();
        SocketAddress socketaddress = new InetSocketAddress("192.168.1.34", 1111);
        socketchannel.connect(socketaddress);

        Socket sock = new Socket("192.168.1.34", 2222);
        DataInputStream input = new DataInputStream(sock.getInputStream());
        DataOutputStream output = new DataOutputStream(sock.getOutputStream());

        int filename = input.readInt();
        for (int i = 0; i < filename; i++) {
            System.out.println((i + 1) + ". " + input.readUTF());
            System.out.println(" Size: " + input.readLong() + " MB");
        }
        int fileselect = scan.nextInt();

        output.writeInt(fileselect);

        String fileName = input.readUTF();
        long fileSize = input.readLong();
        String sendPath = "//home//nongguitar//Documents//client//" + fileName;

        long start = System.currentTimeMillis();
        Path path = Paths.get(sendPath);
        FileChannel filechannel = FileChannel.open(path, StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);
        ByteBuffer bufferbyte = ByteBuffer.allocate(8192);
        while (true) {
            copyfile = socketchannel.read(bufferbyte);
            bufferbyte.flip();
            filechannel.write(bufferbyte);
            bufferbyte.clear();
            total += copyfile;
            if (total == fileSize) {
                break;
            }
        }
        output.writeBoolean(true);
        long end = System.currentTimeMillis();
        long time = end - start;
        System.out.println("Time: " + time + " millisecond");
    }
}