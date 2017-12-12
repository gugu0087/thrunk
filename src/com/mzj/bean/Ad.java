package com.mzj.bean;


/**
 * 广告返回的数据格式
 * @author fyf000
 *
 */
public class Ad implements Cloneable{

	public static String IMAGETYPE = "image";
	public static String VIDEOTYPE = "mp4";
	private String id;//廣告id
	private String src;//地址
	private String fileType;//文件格式
	private String md5;//文件md5值
	private String publish;//开始播放时间
	private String expire;//结束播放时间
	private String duration;//播放时长(s)
	private String videoPlayNum;//视频播放次数
	private String forUser;//是否用户站上体重称时播放
	private String status;//1(上架）2（下架)
	private String defaultAd;//是否是默认广告
	private String fileName;//文件名字
	private String taskId;// 任务id
	private String filePath;// 文件路径

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSrc() {
		return src;
	}
	public void setSrc(String src) {
		this.src = src;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public String getPublish() {
		return publish;
	}
	public void setPublish(String publish) {
		this.publish = publish;
	}
	public String getExpire() {
		return expire;
	}
	public void setExpire(String expire) {
		this.expire = expire;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getVideoPlayNum() {
		return videoPlayNum;
	}
	public void setVideoPlayNum(String videoPlayNum) {
		this.videoPlayNum = videoPlayNum;
	}
	public String getForUser() {
		return forUser;
	}
	public void setForUser(String forUser) {
		this.forUser = forUser;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDefaultAd() {
		return defaultAd;
	}
	public void setDefaultAd(String defaultAd) {
		this.defaultAd = defaultAd;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public Object clone() {
		Ad ad = null;
		try{
			ad = (Ad)super.clone();
		}catch(CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return ad;
	}

	@Override
	public String toString() {
		return "Ad{" +
				"id='" + id + '\'' +
				", src='" + src + '\'' +
				", fileType='" + fileType + '\'' +
				", md5='" + md5 + '\'' +
				", publish='" + publish + '\'' +
				", expire='" + expire + '\'' +
				", duration='" + duration + '\'' +
				", videoPlayNum='" + videoPlayNum + '\'' +
				", forUser='" + forUser + '\'' +
				", status='" + status + '\'' +
				", defaultAd='" + defaultAd + '\'' +
				", fileName='" + fileName + '\'' +
				'}';
	}
}
