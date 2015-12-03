package net.wicp.tams.commons.web.easyuibean;

import java.util.List;

import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.Validate;

import net.wicp.tams.commons.callback.IConvertValue;

/****
 * 用户对象与EasyUINode的转换器
 * 
 * @author Administrator
 * 
 */
public class EasyUINodeConf {
	private String idCol;// id字段名
	private String textCol;// text字段名
	private String parentCol;// 父Id字段名
	private String checkedCol;
	private String indexCol;// index排序字段名
	private String iconClsCol;// 图标字段名 ,支持 ":icon-dept" 表示传入常量icon-dept
	private String isCloseCol;// 如果是目录，标识是否为关闭的字段
	private String[] attrCols;// 附加属性字段集合
	private Predicate isRoot;//
	private List<String> checkedList; // 已选择的节点Id集合
	/***
	 * 文本转换器，传进的对象为业务对象
	 */
	private IConvertValue<Object> textConvert;// 把text转成要显示的文本转换器,通过国际化可用

	public EasyUINodeConf(String idCol, String textCol, String parentCol) {
		Validate.notBlank(idCol);
		Validate.notBlank(textCol);
		Validate.notBlank(parentCol);
		this.idCol = idCol;
		this.textCol = textCol;
		this.parentCol = parentCol;
	}

	/***
	 * 构造没有父的树，这种树都是根节点
	 * 
	 * @param idCol
	 * @param textCol
	 */
	public EasyUINodeConf(String idCol, String textCol) {
		Validate.notBlank(idCol);
		Validate.notBlank(textCol);
		this.idCol = idCol;
		this.textCol = textCol;
	}

	public EasyUINodeConf(String idCol, String textCol, String parentCol, String indexCol) {
		this(idCol, textCol, parentCol);
		this.indexCol = indexCol;
	}

	public EasyUINodeConf(String idCol, String textCol, String parentCol, String indexCol, String iconClsCol) {
		this(idCol, textCol, parentCol, indexCol);
		this.iconClsCol = iconClsCol;
	}

	public String getIdCol() {
		return idCol;
	}

	public void setIdCol(String idCol) {
		this.idCol = idCol;
	}

	public String getTextCol() {
		return textCol;
	}

	public void setTextCol(String textCol) {
		this.textCol = textCol;
	}

	public String getParentCol() {
		return parentCol;
	}

	public void setParentCol(String parentCol) {
		this.parentCol = parentCol;
	}

	public String getCheckedCol() {
		return checkedCol;
	}

	public void setCheckedCol(String checkedCol) {
		this.checkedCol = checkedCol;
	}

	public String getIndexCol() {
		return indexCol;
	}

	public void setIndexCol(String indexCol) {
		this.indexCol = indexCol;
	}

	public String getIconClsCol() {
		return iconClsCol;
	}

	public void setIconClsCol(String iconClsCol) {
		this.iconClsCol = iconClsCol;
	}

	public String getIsCloseCol() {
		return isCloseCol;
	}

	public void setIsCloseCol(String isCloseCol) {
		this.isCloseCol = isCloseCol;
	}

	public String[] getAttrCols() {
		return attrCols;
	}

	public void setAttrCols(String... attrCols) {
		this.attrCols = attrCols;
	}

	public Predicate getIsRoot() {
		return isRoot;
	}

	public void setIsRoot(Predicate isRoot) {
		this.isRoot = isRoot;
	}

	public List<String> getCheckedList() {
		return checkedList;
	}

	public void setCheckedList(List<String> checkedList) {
		this.checkedList = checkedList;
	}

	public IConvertValue<Object> getTextConvert() {
		return textConvert;
	}

	public void setTextConvert(IConvertValue<Object> textConvert) {
		this.textConvert = textConvert;
	}
}
