package com.treatwell.roundabout.app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    @GetMapping("/endpoint")
    public String endpoint(@RequestParam("name") String name) {
        return "Hello " + name;
    }
}
