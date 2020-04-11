package lab;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class CountDownLatchDemo {
    public static class Job implements Runnable{

        private CountDownLatch countDownLatch;
        public Job(CountDownLatch countDownLatch){
            this.countDownLatch = countDownLatch;
        }
        @Override
        public void run() {
//            模拟工作耗时
            Random random = new Random();
            try {
                Thread.sleep(random.nextInt(30)*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //传入10个，每执行完一个线程countDownLatch-1
            countDownLatch.countDown();
            System.out.println(Thread.currentThread()+"结束了...");
        }
    }
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(new Job(countDownLatch));
            thread.start();
        }
//        等待放出去的十个线程都执行完，才结束
        System.out.println("等待线程结束。。");
        countDownLatch.await();
        System.out.println("线程都结束了");
    }
}
