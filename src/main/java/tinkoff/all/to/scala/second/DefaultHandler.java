package tinkoff.all.to.scala.second;

import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DefaultHandler implements Handler {
    @Override
    public Duration timeout() {
        return null;
    }

    @Override
    public void performOperation() {

        BlockingQueue<Data> queue = new LinkedBlockingQueue<>();
        Client client = new DefaultClient();
        int producerThreadCounter = 3;
        int consumerThreadCounter = 3;

        // producer
        for (int i = 0; i < producerThreadCounter; i++) {
            new Thread(() -> {
                while (true) {
                    Event event = client.readData();
                    for (Address address : event.recipients()) {
                        queue.add(new Data(address, event.payload()));
                    }
                }
            }).start();
        }

        // consumer
        for (int i = 0; i < consumerThreadCounter; i++) {
            new Thread(() -> {
                while (true) {
                    if (!queue.isEmpty()) {
                        try {
                            Data data = queue.take();
                            while (true) {
                                Result response = client.sendData(data.dest, data.payload);
                                if (response.equals(Result.REJECTED)) {
                                    Thread.sleep(timeout().toMillis());
                                } else {
                                    break;
                                }
                            }
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }).start();
        }
    }
}
