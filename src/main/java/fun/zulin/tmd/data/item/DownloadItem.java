package fun.zulin.tmd.data.item;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.tangzc.autotable.annotation.AutoTable;
import com.tangzc.autotable.annotation.Ignore;
import com.tangzc.autotable.annotation.PrimaryKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Builder
@TableName("download_item")
@AutoTable("download_item")
@NoArgsConstructor
@AllArgsConstructor
public class DownloadItem {

    @TableId(type = IdType.AUTO)
    @PrimaryKey(true)
    private Long id;


    private String filename;

    private Integer fileId;

    private Long massageId;


    private String uniqueId;


    private long fileSize;


    private long downloadedSize;


    private String caption;

    @Ignore
    @TableField(exist = false)
    private Float progress;

    private String state;


    /**
     * 每隔n次统计速度
     */
    @Ignore
    @TableField(exist = false)
    private int downloadCount;

    public long getDownloadBytePerSec() {
        var timeDiff = Duration.between(this.getDownloadUpdateTime(), LocalDateTime.now()).toSeconds();
        if (timeDiff > 2) {
            return 0;
        }
        return downloadBytePerSec;
    }

    @Ignore
    @TableField(exist = false)
    private long downloadBytePerSec;

    public LocalDateTime getDownloadUpdateTime() {
        if (downloadUpdateTime == null) {
            return LocalDateTime.now();
        }
        return downloadUpdateTime;
    }

    @Ignore
    @TableField(exist = false)
    private LocalDateTime downloadUpdateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;


    public Float getProgress() {
        return (float) (downloadedSize) / (float) (fileSize) * 100;
    }

}
