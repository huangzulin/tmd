package fun.zulin.tmd.telegram.handler;

import fun.zulin.tmd.telegram.DownloadManage;
import it.tdlight.jni.TdApi;

public class UpdateFileHandler {

    public static void accept(TdApi.UpdateFile update) {

        DownloadManage.updateProgress(update.file.remote.uniqueId, update.file.local.downloadedSize);

    }

}
