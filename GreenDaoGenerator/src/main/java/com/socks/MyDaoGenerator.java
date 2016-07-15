package com.socks;

/**
 * Created by zhaokaiqiang on 15/5/11.
 */

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

/**
 * 用来为GreenDao框架生成Dao文件
 */
public class MyDaoGenerator {

	//辅助文件生成的相对路径  ，指定输出的目录
	public static final String DAO_PATH = "../app/src/main/java-gen";
	//辅助文件的包名
	public static final String PACKAGE_NAME = "com.socks.greendao";
	//数据库的版本号
	public static final int DATA_VERSION_CODE = 1;

	public static void main(String[] args) throws Exception {

		// // 正如你所见的，你创建了一个用于添加实体（Entity）的模式（Schema）对象。
		// 两个参数分别代表：数据库版本号与自动生成代码的包路径。
		Schema schema = new Schema(DATA_VERSION_CODE, PACKAGE_NAME);
		addCache(schema, "JokeCache");
		addCache(schema, "FreshNewsCache");
		addCache(schema, "PictureCache");
		addCache(schema, "SisterCache");
		addCache(schema, "VideoCache");
		//生成Dao文件路径  使用 DAOGenerator 类的 generateAll() 方法自动生成代码
		new DaoGenerator().generateAll(schema, DAO_PATH);

	}

	/**
	 * 添加不同的缓存表
	 * @param schema
	 * @param tableName
	 */
	private static void addCache(Schema schema, String tableName) {
		// 一个实体（类）就关联到数据库中的一张表，此处表名为「tableName」（既类名）
		Entity joke = schema.addEntity(tableName);
         //设置表的字段

		//主键id，接口请求数据result，页码page，添加时间time
		//主键id自增长
		joke.addIdProperty().primaryKey().autoincrement();
		//请求结果
		joke.addStringProperty("result");
		//页数
		joke.addIntProperty("page");
		//插入时间，暂时无用
		joke.addLongProperty("time");

	}

}