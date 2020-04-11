package lab;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class 配置Demo {
    public static void main(String[] args) throws IOException {
        //读取配置文件作为输入流
        InputStream is = 配置Demo.class.getClassLoader().getResourceAsStream("1.properties");
        Properties properties = new Properties();
        //从输入流中加载配置文件
        properties.load(is);
        //获取配置文件中的值
        String user = (String) properties.get("user");
        System.out.println(user);
    }
}
