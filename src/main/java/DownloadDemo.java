/**
 * Created by LiuQiulan
 *
 * @date 2018-7-20 14:28
 */
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
public class DownloadDemo {
    //构造一个带指定Zone对象的配置类
   Configuration cfg = new Configuration(Zone.zone1());
    //密钥配置
    Auth auth = new QiniuConfig().returnAuth();
    BucketManager bucketManager = new BucketManager(auth,cfg);

    //构造私有空间的需要生成的下载的链接
    //标准情况下需要在拼接链接之前，将文件名进行urlencode以兼容不同的字符
    //String encodedFileName = URLEncoder.encode(fileName, "utf-8");
    //String finalUrl = String.format("%s/%s", domainOfBucket, encodedFileName);

    String baseUrl = "http://" + "social.qiniu.doctornong.com" + "/" + "social/banner/35553cv1gDgT1vsYTT994c53mvTf1Y9s";
    public void download() throws QiniuException {
        //调用privateDownloadUrl方法生成下载链接,第二个参数可以设置Token的过期时间
        String downloadRUL = auth.privateDownloadUrl(baseUrl,3600);
        System.out.println(downloadRUL);
        FileInfo fileInfo = bucketManager.stat("social", "social/banner/35553cv1gDgT1vsYTT994c53mvTf1Y9s");
        System.out.println(fileInfo.hash);
        System.out.println(fileInfo.fsize);
        System.out.println(fileInfo.mimeType);
        System.out.println(fileInfo.putTime);

    }
    public static void main(String args[]) throws QiniuException {
        new DownloadDemo().download();
    }
}
