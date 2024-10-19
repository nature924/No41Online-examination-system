package com.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.lang.reflect.InvocationTargetException;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.beanutils.BeanUtils;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.enums.FieldFill;
import com.baomidou.mybatisplus.enums.IdType;

/**
 * 考试记录表
 *
 * @author 
 * @email
 */
@TableName("examrecord")
public class ExamrecordEntity<T> implements Serializable {
    private static final long serialVersionUID = 1L;


	public ExamrecordEntity() {

	}

	public ExamrecordEntity(T t) {
		try {
			BeanUtils.copyProperties(this, t);
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")

    private Integer id;


    /**
     * 考试编号
     */
    @TableField(value = "examrecord_uuid_number")

    private String examrecordUuidNumber;


    /**
     * 考试学生
     */
    @TableField(value = "yonghu_id")

    private Integer yonghuId;


    /**
     * 所属试卷id（外键）
     */
    @TableField(value = "exampaper_id")

    private Integer exampaperId;


    /**
     * 所得总分
     */
    @TableField(value = "total_score")

    private Integer totalScore;


    /**
     * 考试时间
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat
    @TableField(value = "insert_time",fill = FieldFill.INSERT)

    private Date insertTime;


    /**
     * 创建时间
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat
    @TableField(value = "create_time",fill = FieldFill.INSERT)

    private Date createTime;


    /**
	 * 设置：主键
	 */
    public Integer getId() {
        return id;
    }
    /**
	 * 获取：主键
	 */

    public void setId(Integer id) {
        this.id = id;
    }
    /**
	 * 设置：考试编号
	 */
    public String getExamrecordUuidNumber() {
        return examrecordUuidNumber;
    }
    /**
	 * 获取：考试编号
	 */

    public void setExamrecordUuidNumber(String examrecordUuidNumber) {
        this.examrecordUuidNumber = examrecordUuidNumber;
    }
    /**
	 * 设置：考试学生
	 */
    public Integer getYonghuId() {
        return yonghuId;
    }
    /**
	 * 获取：考试学生
	 */

    public void setYonghuId(Integer yonghuId) {
        this.yonghuId = yonghuId;
    }
    /**
	 * 设置：所属试卷id（外键）
	 */
    public Integer getExampaperId() {
        return exampaperId;
    }
    /**
	 * 获取：所属试卷id（外键）
	 */

    public void setExampaperId(Integer exampaperId) {
        this.exampaperId = exampaperId;
    }
    /**
	 * 设置：所得总分
	 */
    public Integer getTotalScore() {
        return totalScore;
    }
    /**
	 * 获取：所得总分
	 */

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }
    /**
	 * 设置：考试时间
	 */
    public Date getInsertTime() {
        return insertTime;
    }
    /**
	 * 获取：考试时间
	 */

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }
    /**
	 * 设置：创建时间
	 */
    public Date getCreateTime() {
        return createTime;
    }
    /**
	 * 获取：创建时间
	 */

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Examrecord{" +
            "id=" + id +
            ", examrecordUuidNumber=" + examrecordUuidNumber +
            ", yonghuId=" + yonghuId +
            ", exampaperId=" + exampaperId +
            ", totalScore=" + totalScore +
            ", insertTime=" + insertTime +
            ", createTime=" + createTime +
        "}";
    }
}
