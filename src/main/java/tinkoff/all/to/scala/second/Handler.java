package tinkoff.all.to.scala.second;

import java.time.Duration;

public interface Handler {
    Duration timeout();

    void performOperation();
}
