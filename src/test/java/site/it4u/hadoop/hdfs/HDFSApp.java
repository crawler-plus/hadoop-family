package site.it4u.hadoop.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.swing.plaf.PanelUI;
import java.net.URI;

/**
 * hadoop hdfs java API
 */
public class HDFSApp {

    FileSystem fileSystem = null;

    Configuration configuration = null;

    public static final String HDFS_PATH = "hdfs://192.168.0.110:8020";

    @Before
    public void setUp() throws Exception {
        System.out.println(this.getClass().getSimpleName() + "set up");
        configuration = new Configuration();
        fileSystem = FileSystem.get(new URI(HDFS_PATH), configuration, "root");
    }

    @After
    public void tearDown() throws Exception {
        configuration = null;
        fileSystem = null;
        System.out.println(this.getClass().getSimpleName() + "tear down");
    }

    /**
     * 创建文件夹
     * @throws Exception
     */
    @Test
    public void mkdir() throws Exception {
        fileSystem.mkdirs(new Path("/hdfsapi/test"));
    }

    /**
     * 创建文件
     * @throws Exception
     */
    @Test
    public void create() throws Exception {
        FSDataOutputStream fsDataOutputStream = fileSystem.create(new Path("/hdfsapi/test/a.txt"));
        fsDataOutputStream.write("hello hadoop".getBytes());
        fsDataOutputStream.flush();
        fsDataOutputStream.close();
    }

    /**
     * 查看hdfs文件的内容
     * @throws Exception
     */
    @Test
    public void cat() throws Exception {
        FSDataInputStream fsDataInputStream = fileSystem.open(new Path("/hdfsapi/test/a.txt"));
        IOUtils.copyBytes(fsDataInputStream, System.out, 1024);
        fsDataInputStream.close();
    }

    /**
     * 重命名
     * @throws Exception
     */
    @Test
    public void rename() throws Exception {
        fileSystem.rename(new Path("/hdfsapi/test/a.txt"),
                new Path("/hdfsapi/test/b.txt"));
    }

    /**
     * 上传文件到HDFS
     * @throws Exception
     */
    @Test
    public void copyFromLocalFile() throws Exception {
        Path localPath = new Path("hadoop_local_file.txt");
        Path hdfsPath = new Path("/hdfsapi/test");
        fileSystem.copyFromLocalFile(localPath, hdfsPath);
    }

    /**
     * 下载到本地
     * @throws Exception
     */
    @Test
    public void copyToLocalFile() throws Exception {
        Path localPath = new Path("hadoop_local_file_down.txt");
        Path hdfsPath = new Path("/hdfsapi/test/hadoop_local_file.txt");
        fileSystem.copyToLocalFile(hdfsPath, localPath);
    }

    /**
     * 查看某个目录下的所有文件
     * @throws Exception
     */
    @Test
    public void listFiles() throws Exception {
        FileStatus[] fileStatuses = fileSystem.listStatus(new Path("/hdfsapi/test"));
        for (FileStatus fileStatus : fileStatuses) {
            String isDir = fileStatus.isDirectory() ? "文件夹" : "文件";
            short replication = fileStatus.getReplication();
            long len = fileStatus.getLen();
            String path = fileStatus.getPath().toString();
            System.out.println(isDir + "\t" + replication + "\t" + len + "\t" + path);
        }
    }

    /**
     * 删除
     * @throws Exception
     */
    @Test
    public void delete() throws Exception {
        // 递归删除
        fileSystem.delete(new Path("/hdfsapi/test"), true);
    }

}
