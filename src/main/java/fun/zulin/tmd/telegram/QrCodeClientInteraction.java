package fun.zulin.tmd.telegram;

import fun.zulin.tmd.utils.SpringContext;
import it.tdlight.client.ClientInteraction;
import it.tdlight.client.InputParameter;
import it.tdlight.client.ParameterInfo;
import it.tdlight.client.ParameterInfoNotifyLink;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.concurrent.CompletableFuture;


public class QrCodeClientInteraction implements ClientInteraction {

    @Override
    public CompletableFuture<String> onParameterRequest(InputParameter parameter, ParameterInfo parameterInfo) {

        return CompletableFuture.supplyAsync(() -> {

            System.out.println(parameter);
            System.out.println(parameterInfo);

            if (parameterInfo instanceof ParameterInfoNotifyLink info) {
                var link = info.getLink();
                var simpMessagingTemplate = SpringContext.getBean(SimpMessagingTemplate.class);
                simpMessagingTemplate.convertAndSend("/topic/qrcode", link);
            }

            return "";
        });
    }

}
