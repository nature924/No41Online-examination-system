package com.entity.view;

import com.entity.ExamrewrongquestionEntity;
import com.baomidou.mybatisplus.annotations.TableName;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;

/**
 * 错题表
 * 后端返回视图实体辅助类
 * （通常后端关联的表或者自定义的字段需要返回使用）
 */
@TableName("examrewrongquestion")
public class ExamrewrongquestionView extends ExamrewrongquestionEntity implements Serializable {
    private static final long serialVersionUID = 1L;




		//级联表 exampaper
			/**
			* 试卷名称
			*/
			private String exampaperName;
			/**
			* 考试时长(分钟)
			*/
			private Integer exampaperDate;
			/**
			* 试卷总分数
			*/
			private Integer exampaperMyscore;
			/**
			* 试卷状态
			*/
			private Integer exampaperTypes;
				/**
				* 试卷状态的值
				*/
				private String exampaperValue;
			/**
			* 逻辑删除（0代表未删除 1代表已删除）
			*/
			private Integer exampaperDelete;

		//级联表 examquestion
			/**
			* 试题名称
			*/
			private String examquestionName;
			/**
			* 选项，json字符串
			*/
			private String examquestionOptions;
			/**
			* 分值
			*/
			private Integer examquestionScore;
			/**
			* 正确答案
			*/
			private String examquestionAnswer;
			/**
			* 答案解析
			*/
			private String examquestionAnalysis;
			/**
			* 试题类型
			*/
			private Integer examquestionTypes;
				/**
				* 试题类型的值
				*/
				private String examquestionValue;
			/**
			* 试题排序，值越大排越前面
			*/
			private Integer examquestionSequence;

		//级联表 yonghu
			/**
			* 学号
			*/
			private String yonghuUuidNumber;
			/**
			* 学生姓名
			*/
			private String yonghuName;
			/**
			* 学生手机号
			*/
			private String yonghuPhone;
			/**
			* 学生身份证号
			*/
			private String yonghuIdNumber;
			/**
			* 学生头像
			*/
			private String yonghuPhoto;
			/**
			* 班级
			*/
			private Integer banjiTypes;
				/**
				* 班级的值
				*/
				private String banjiValue;
			/**
			* 电子邮箱
			*/
			private String yonghuEmail;

	public ExamrewrongquestionView() {

	}

	public ExamrewrongquestionView(ExamrewrongquestionEntity examrewrongquestionEntity) {
		try {
			BeanUtils.copyProperties(this, examrewrongquestionEntity);
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}












				//级联表的get和set exampaper

					/**
					* 获取： 试卷名称
					*/
					public String getExampaperName() {
						return exampaperName;
					}
					/**
					* 设置： 试卷名称
					*/
					public void setExampaperName(String exampaperName) {
						this.exampaperName = exampaperName;
					}

					/**
					* 获取： 考试时长(分钟)
					*/
					public Integer getExampaperDate() {
						return exampaperDate;
					}
					/**
					* 设置： 考试时长(分钟)
					*/
					public void setExampaperDate(Integer exampaperDate) {
						this.exampaperDate = exampaperDate;
					}

					/**
					* 获取： 试卷总分数
					*/
					public Integer getExampaperMyscore() {
						return exampaperMyscore;
					}
					/**
					* 设置： 试卷总分数
					*/
					public void setExampaperMyscore(Integer exampaperMyscore) {
						this.exampaperMyscore = exampaperMyscore;
					}

					/**
					* 获取： 试卷状态
					*/
					public Integer getExampaperTypes() {
						return exampaperTypes;
					}
					/**
					* 设置： 试卷状态
					*/
					public void setExampaperTypes(Integer exampaperTypes) {
						this.exampaperTypes = exampaperTypes;
					}


						/**
						* 获取： 试卷状态的值
						*/
						public String getExampaperValue() {
							return exampaperValue;
						}
						/**
						* 设置： 试卷状态的值
						*/
						public void setExampaperValue(String exampaperValue) {
							this.exampaperValue = exampaperValue;
						}

					/**
					* 获取： 逻辑删除（0代表未删除 1代表已删除）
					*/
					public Integer getExampaperDelete() {
						return exampaperDelete;
					}
					/**
					* 设置： 逻辑删除（0代表未删除 1代表已删除）
					*/
					public void setExampaperDelete(Integer exampaperDelete) {
						this.exampaperDelete = exampaperDelete;
					}





				//级联表的get和set examquestion


					/**
					* 获取： 试题名称
					*/
					public String getExamquestionName() {
						return examquestionName;
					}
					/**
					* 设置： 试题名称
					*/
					public void setExamquestionName(String examquestionName) {
						this.examquestionName = examquestionName;
					}

					/**
					* 获取： 选项，json字符串
					*/
					public String getExamquestionOptions() {
						return examquestionOptions;
					}
					/**
					* 设置： 选项，json字符串
					*/
					public void setExamquestionOptions(String examquestionOptions) {
						this.examquestionOptions = examquestionOptions;
					}

					/**
					* 获取： 分值
					*/
					public Integer getExamquestionScore() {
						return examquestionScore;
					}
					/**
					* 设置： 分值
					*/
					public void setExamquestionScore(Integer examquestionScore) {
						this.examquestionScore = examquestionScore;
					}

					/**
					* 获取： 正确答案
					*/
					public String getExamquestionAnswer() {
						return examquestionAnswer;
					}
					/**
					* 设置： 正确答案
					*/
					public void setExamquestionAnswer(String examquestionAnswer) {
						this.examquestionAnswer = examquestionAnswer;
					}

					/**
					* 获取： 答案解析
					*/
					public String getExamquestionAnalysis() {
						return examquestionAnalysis;
					}
					/**
					* 设置： 答案解析
					*/
					public void setExamquestionAnalysis(String examquestionAnalysis) {
						this.examquestionAnalysis = examquestionAnalysis;
					}

					/**
					* 获取： 试题类型
					*/
					public Integer getExamquestionTypes() {
						return examquestionTypes;
					}
					/**
					* 设置： 试题类型
					*/
					public void setExamquestionTypes(Integer examquestionTypes) {
						this.examquestionTypes = examquestionTypes;
					}


						/**
						* 获取： 试题类型的值
						*/
						public String getExamquestionValue() {
							return examquestionValue;
						}
						/**
						* 设置： 试题类型的值
						*/
						public void setExamquestionValue(String examquestionValue) {
							this.examquestionValue = examquestionValue;
						}

					/**
					* 获取： 试题排序，值越大排越前面
					*/
					public Integer getExamquestionSequence() {
						return examquestionSequence;
					}
					/**
					* 设置： 试题排序，值越大排越前面
					*/
					public void setExamquestionSequence(Integer examquestionSequence) {
						this.examquestionSequence = examquestionSequence;
					}


































				//级联表的get和set yonghu

					/**
					* 获取： 学号
					*/
					public String getYonghuUuidNumber() {
						return yonghuUuidNumber;
					}
					/**
					* 设置： 学号
					*/
					public void setYonghuUuidNumber(String yonghuUuidNumber) {
						this.yonghuUuidNumber = yonghuUuidNumber;
					}

					/**
					* 获取： 学生姓名
					*/
					public String getYonghuName() {
						return yonghuName;
					}
					/**
					* 设置： 学生姓名
					*/
					public void setYonghuName(String yonghuName) {
						this.yonghuName = yonghuName;
					}

					/**
					* 获取： 学生手机号
					*/
					public String getYonghuPhone() {
						return yonghuPhone;
					}
					/**
					* 设置： 学生手机号
					*/
					public void setYonghuPhone(String yonghuPhone) {
						this.yonghuPhone = yonghuPhone;
					}

					/**
					* 获取： 学生身份证号
					*/
					public String getYonghuIdNumber() {
						return yonghuIdNumber;
					}
					/**
					* 设置： 学生身份证号
					*/
					public void setYonghuIdNumber(String yonghuIdNumber) {
						this.yonghuIdNumber = yonghuIdNumber;
					}

					/**
					* 获取： 学生头像
					*/
					public String getYonghuPhoto() {
						return yonghuPhoto;
					}
					/**
					* 设置： 学生头像
					*/
					public void setYonghuPhoto(String yonghuPhoto) {
						this.yonghuPhoto = yonghuPhoto;
					}

					/**
					* 获取： 班级
					*/
					public Integer getBanjiTypes() {
						return banjiTypes;
					}
					/**
					* 设置： 班级
					*/
					public void setBanjiTypes(Integer banjiTypes) {
						this.banjiTypes = banjiTypes;
					}


						/**
						* 获取： 班级的值
						*/
						public String getBanjiValue() {
							return banjiValue;
						}
						/**
						* 设置： 班级的值
						*/
						public void setBanjiValue(String banjiValue) {
							this.banjiValue = banjiValue;
						}

					/**
					* 获取： 电子邮箱
					*/
					public String getYonghuEmail() {
						return yonghuEmail;
					}
					/**
					* 设置： 电子邮箱
					*/
					public void setYonghuEmail(String yonghuEmail) {
						this.yonghuEmail = yonghuEmail;
					}






}
