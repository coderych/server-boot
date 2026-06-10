package com.coderych.server.controller;

import com.coderych.commons.core.model.R;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {
    @RequestMapping("/")
    public R<String> index() {
        return R.ok("Welcome!!!");
    }
}
