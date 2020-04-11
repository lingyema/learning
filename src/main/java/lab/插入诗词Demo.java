package lab;



import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class 插入诗词Demo {
    public static void main(String[] args) throws SQLException {
        String 朝代 = "唐代";
        String 题目 = "登鹳雀楼";
        String 作者 = "王之涣";
        String 正文 = "白日依山尽，黄河入海流。欲穷千里目，更上一层楼。";

        //通过DataSource获取connection(带有连接池，好处参照线程池)
        MysqlConnectionPoolDataSource dataSource = new MysqlConnectionPoolDataSource();
        dataSource.setServerName("127.0.0.1");
        dataSource.setPort(3306);
        dataSource.setUser("root");
        dataSource.setPassword("123");
        dataSource.setDatabaseName("tangshi");
        dataSource.setUseSSL(false);
        dataSource.setCharacterEncoding("UTF8");
        Connection connection = dataSource.getConnection();

        //sql语句
        String sql = "insert into tangshi(sha256,dynasty,title,author,content,words)" +
                "values(?,?,?,?,?,?)";

        //获取PreparedStatement执行sql
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1,"sha256");
        statement.setString(2,朝代);
        statement.setString(3,题目);
        statement.setString(4,作者);
        statement.setString(5,正文);
        statement.setString(6,"");

        statement.executeUpdate();
    }
}
