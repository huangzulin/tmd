package fun.zulin.tmd.controller;

import fun.zulin.tmd.telegram.Tmd;
import it.tdlight.jni.TdApi;
import org.springframework.web.bind.annotation.*;

@RestController
public class WebController {


    @GetMapping("me")
    public TdApi.User me() {
        return Tmd.me;
    }

    @PostMapping("logout")
    public String logout() {
        if (Tmd.client != null) {
            Tmd.client.send(new TdApi.LogOut(), res -> {
                try {
                    Tmd.client.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            Tmd.me = null;
        }

        return "ok";
    }
}
