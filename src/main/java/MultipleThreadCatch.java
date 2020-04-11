import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;

import javax.sql.DataSource;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class MultipleThreadCatch {
    private static AtomicInteger successCount = new AtomicInteger(0);
    private static AtomicInteger failureCount = new AtomicInteger(0);
    public static class Job implements Runnable{
        private String url;
        private DataSource dataSource;
        public Job(String url, DataSource dataSource){
            this.url = url;
            this.dataSource = dataSource;
        }
        @Override
        public void run() {
            WebClient webClient = new WebClient(BrowserVersion.CHROME);
            webClient.getOptions().setJavaScriptEnabled(false);
            webClient.getOptions().setCssEnabled(false);
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                //得到详情页
                HtmlPage page = webClient.getPage(url);
                String xpath;
                DomText domText;
                //题目
                xpath="//div[@class='cont']/h1/text()";
                domText = (DomText) page.getBody().getByXPath(xpath).get(0);
                String title = domText.asText();
                //朝代
                xpath = "//div[@class='cont']/p[@class='source']/a[1]/text()";
                domText = (DomText) page.getBody().getByXPath(xpath).get(0);
                String dynasty = domText.asText();
                //作者
                xpath = "//div[@class='cont']/p[@class='source']/a[2]/text()";
                domText = (DomText) page.getBody().getByXPath(xpath).get(0);
                String author = domText.asText();
                //正文
                xpath = "//div[@class='cont']/div[@class='contson']";
                HtmlElement element = (HtmlElement) page.getBody().getByXPath(xpath).get(0);
                String context = element.getTextContent().trim();

                //计算sha256
                String s = title + context;
                messageDigest.update(s.getBytes());
                byte[] result = messageDigest.digest();
                //使用StringBuilder，将result存进去
                StringBuilder sha256 = new StringBuilder();
                for (byte b : result){
//                以十六进制存入
                    sha256.append(String.format("%02x",b));
                }

                //计算分词
//         termList存储需要分词的内容（题目和文章）
                List<Term> termList = new ArrayList<>();
                //addAll将list集合中的东西全部加进去
                termList.addAll(NlpAnalysis.parse(title).getTerms());
                termList.addAll(NlpAnalysis.parse(context).getTerms());
//          words存储符合条件的分词
                List<String> words = new ArrayList<>();
                for (Term term : termList){
                    if (term.getNatureStr().equals("w")){
                        continue;
                    }
                    if (term.getNatureStr().equals("null")){
                        continue;
                    }
                    if (term.getRealName().length()<2){
                        continue;
                    }
                    //得到分词不是标点，不为空，不是一个字则加入words中
                    words.add(term.getRealName());
                }
                //将各分词之间用，隔开；insetWords里面是每首诗的分词
                String insetWords = String.join(",",words);
                //获取数据库连接
                try(Connection connection = dataSource.getConnection()){
                    String sql = "insert into tangshi(sha256,dynasty,title,author,content,words)" +
                            "values(?,?,?,?,?,?)";

                    try(PreparedStatement statement = connection.prepareStatement(sql)){
                        statement.setString(1,sha256.toString());
                        statement.setString(2,dynasty);
                        statement.setString(3,title);
                        statement.setString(4,author);
                        statement.setString(5,context);
                        statement.setString(6,insetWords);
//           强转用来看sql语句的执行情况
                        com.mysql.jdbc.PreparedStatement mysqlState = (com.mysql.jdbc.PreparedStatement) statement;
                        System.out.println(mysqlState.asSql());

//            开始执行
                        statement.executeUpdate();
                    }
                }
                successCount.getAndIncrement();
            } catch (IOException e) {
                e.printStackTrace();
                failureCount.getAndIncrement();
            } catch (SQLException e) {
                if (!e.getMessage().contains("Duplicate entry")){
                    e.printStackTrace();
                    failureCount.getAndIncrement();
                }else {
                    successCount.getAndIncrement();
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                failureCount.getAndIncrement();
            }
        }
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(30);
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        String baseUrl = "https://so.gushiwen.cn";
        String pathUrl = "/gushi/tangshi.aspx";

        //detailUrlList存储列表页中每首诗的详细地址
        List<String> detailUrlList = new ArrayList<>();
        //列表页解析
        {
            String url = baseUrl + pathUrl;
            HtmlPage page = webClient.getPage(url);
            List<HtmlElement> divs = page.getBody().getElementsByAttribute("div", "class", "typecont");
            for (HtmlElement div : divs){
                List<HtmlElement> as = div.getElementsByTagName("a");
                for (HtmlElement a : as){
                    String href = a.getAttribute("href");
                    detailUrlList.add(baseUrl+href);
                }
            }
        }
        //MessageDigest线程安全，创建一个直接传入即可
//        CountDownLatch countDownLatch = new CountDownLatch(detailUrlList.size());
        //dataSource线程安全，创建一个直接传入即可
        MysqlConnectionPoolDataSource dataSource = new MysqlConnectionPoolDataSource();
        dataSource.setServerName("127.0.0.1");
        dataSource.setPort(3306);
        dataSource.setUser("root");
        dataSource.setPassword("123");
        dataSource.setDatabaseName("tangshi");
        dataSource.setUseSSL(false);
        dataSource.setCharacterEncoding("UTF8");
        //详情页请求和解析，一个URL就是一个线程
        for (String url : detailUrlList){
            pool.execute(new Job(url,dataSource));
        }
        while (successCount.get()+failureCount.get() < detailUrlList.size()){


        }
        pool.shutdown();

    }
}
