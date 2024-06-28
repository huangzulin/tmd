package fun.zulin.tmd.data.item;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DownloadItemService extends IService<DownloadItem> {


    DownloadItem getByUniqueId(String uniqueId);

    List<DownloadItem> getDownloadedItem();

    List<DownloadItem> getDownloadingItemsFromDB();

    List<DownloadItem> getDownloading();
}
