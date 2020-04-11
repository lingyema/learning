package lab;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha156Demo {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        //getInstance()中传入支持的方法，MD5，SHA-256，常用SHA-256，MD5短，易冲突
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        String s = "你好世界";
        byte[] bytes = s.getBytes();
        //update()需传入字节或者字节数组
        messageDigest.update(bytes);
        //传入后加密
        byte[] result = messageDigest.digest();
        System.out.println(result.length);

        for (Byte b: result){
            //打印出十六进制的字节
            System.out.printf("%02x",b);
        }
    }
}
