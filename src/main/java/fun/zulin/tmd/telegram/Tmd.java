package fun.zulin.tmd.telegram;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import fun.zulin.tmd.telegram.handler.AuthorizationStateWaitOtherDeviceConfirmationHandler;
import fun.zulin.tmd.telegram.handler.UpdateFileHandler;
import fun.zulin.tmd.telegram.handler.UpdateNewMessageHandler;
import fun.zulin.tmd.utils.SpringContext;
import it.tdlight.Init;
import it.tdlight.Log;
import it.tdlight.Slf4JLogMessageHandler;
import it.tdlight.client.*;
import it.tdlight.jni.TdApi;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;


@Component
public class Tmd {

    public static SimpleTelegramClient client;

    public static TdApi.User me;

    public static TdApi.Chat savedMessagesChat;

    private final static SimpleTelegramClientFactory clientFactory = new SimpleTelegramClientFactory();


    @EventListener(ApplicationReadyEvent.class)
    public void init() throws Exception {

        Init.init();
        Log.setLogMessageHandler(1, new Slf4JLogMessageHandler());

        var appId = System.getenv("APP_ID");
        var apiHash = System.getenv("API_HASH");
        var test = Boolean.valueOf(StrUtil.emptyToDefault(System.getenv("Test"), "false"));

        APIToken apiToken = new APIToken(Convert.toInt(appId), apiHash);
        TDLibSettings settings = TDLibSettings.create(apiToken);

        var dataPath = Paths.get("data");
        Files.createDirectories(dataPath);
        var downloadsPath = Paths.get("downloads");
        Files.createDirectories(downloadsPath);
        settings.setDatabaseDirectoryPath(dataPath);
        settings.setDownloadedFilesDirectoryPath(downloadsPath);
        settings.setUseTestDatacenter(test);

        SimpleTelegramClientBuilder clientBuilder = clientFactory.builder(settings);


        SimpleAuthenticationSupplier<?> authenticationData = AuthenticationSupplier.qrCode();

        clientBuilder.addUpdateHandler(TdApi.UpdateAuthorizationState.class, this::onUpdateAuthorizationState);
        clientBuilder.addUpdateHandler(TdApi.UpdateNewMessage.class, UpdateNewMessageHandler::accept);
        clientBuilder.addUpdateHandler(TdApi.UpdateFile.class, UpdateFileHandler::accept);

        var clientInteraction = new QrCodeClientInteraction();

        clientBuilder.addUpdateHandler(TdApi.UpdateAuthorizationState.class,
                new AuthorizationStateWaitOtherDeviceConfirmationHandler(clientInteraction)
        );

        clientBuilder.setClientInteraction(clientInteraction);
        client = clientBuilder.build(authenticationData);

        var meAsync = client.getMeAsync();
        me = meAsync.get();
        if (me != null) {
            savedMessagesChat = client.send(new TdApi.CreatePrivateChat(me.id, true)).get(1, TimeUnit.MINUTES);
            //开始下载未完成任务
            DownloadManage.startDownloading();
        }

    }

    private void onUpdateAuthorizationState(TdApi.UpdateAuthorizationState update) {
        TdApi.AuthorizationState authorizationState = update.authorizationState;
        if (authorizationState instanceof TdApi.AuthorizationStateReady) {
            System.out.println("Logged in");

            var simpMessagingTemplate = SpringContext.getBean(SimpMessagingTemplate.class);
            simpMessagingTemplate.convertAndSend("/topic/auth", "ok");

        } else if (authorizationState instanceof TdApi.AuthorizationStateClosing) {
            System.out.println("Closing...");
        } else if (authorizationState instanceof TdApi.AuthorizationStateClosed) {
            System.out.println("Closed");
        } else if (authorizationState instanceof TdApi.AuthorizationStateLoggingOut) {
            System.out.println("Logging out...");
        }
    }


    public SimpleTelegramClientFactory getClientFactory() {
        return clientFactory;
    }


}
