package com.mzj.util;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


/**
 * http工具类
 * @author fyf000
 *
 */
public class HttpUtils {

	public final static OkHttpClient client = new OkHttpClient();
	static {
		client.setConnectTimeout(15, TimeUnit.SECONDS);
		client.setReadTimeout(1000, TimeUnit.SECONDS);
		client.setWriteTimeout(1000, TimeUnit.SECONDS);
	}

	public final static OkHttpClient clientWeight = new OkHttpClient();
	static {
		clientWeight.setConnectTimeout(15, TimeUnit.SECONDS);
		clientWeight.setReadTimeout(15, TimeUnit.SECONDS);
	}


	/**
	 * 同步发送post请求
	 * @param url
	 * @param params
	 * @return
	 */
	public static Response postSynchronization(String url, JSONObject params) {
		// 通过请求地址和请求体构造Post请求对象Request
		client.setConnectTimeout(10, TimeUnit.SECONDS);
		client.setReadTimeout(100, TimeUnit.SECONDS);
		Response response = null;
		Request request = new Request.Builder().url(url).post(RequestBody.create(
				MediaType.parse("application/json; charset=utf-8"),
				params.toString())).build();

		// 根据Request对象发起Get同步Http请求
		try {
			response = client.newCall(request).execute();
		} catch (Exception e) {
			MzjLog.i("HttpUtils", "执行请求发生异常！"+e.getMessage());
			MzjLog.i("HttpUtils.postSynchronization", "执行请求发生2异常！"+e.toString());
			e.printStackTrace();

		}
		return response;
	}



	/**
	 * 异步发送请求 get
	 * @param url
	 * @param
	 * @return
	 */
	public static Request getAsynchronous(String url){
		Request request = new Request.Builder().url(url).build();
		return request;
	}


	/**
	 * 异步发送请求
	 * @param url
	 * @param params
	 * @return
	 */
	public static Request postAsynchronous(String url, JSONObject params){
		Request request = new Request.Builder().url(url).post(RequestBody.create(
				MediaType.parse("application/json; charset=utf-8"),
				params.toString())).build();
		return request;
	}


	/**
	 * 上传文件
	 */
	public boolean upLoadFile(String url, String filePath) {
		boolean flag;
		//创建File
		File file = new File(filePath);
		//创建RequestBody
		RequestBody body = RequestBody.create(MediaType.parse("application/octet-stream"), file);
		//创建Request
		final Request request = new Request.Builder().url(url).post(body).build();
		Response response = null;
		try {
			response = client.newCall(request).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(response.isSuccessful()){
			MzjLog.i("上传结果", true+"");
			flag = true;
		}else{
			MzjLog.i("上传结果", false+"");
			flag = false;
		}
		return flag;
	}
}