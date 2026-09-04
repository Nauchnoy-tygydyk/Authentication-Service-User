package org.example.paymentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "random-service", url = "https://www.random.org")
public interface IRandomClientNumber {

  @GetMapping("/integers/?num=1&min=1&max=100&col=1&base=10&format=plain&rnd=new")
  Integer getPrediction();
}
