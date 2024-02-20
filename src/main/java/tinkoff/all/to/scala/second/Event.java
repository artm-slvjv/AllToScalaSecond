package tinkoff.all.to.scala.second;

import java.util.List;

public record Event(List<Address> recipients, Payload payload) {}
