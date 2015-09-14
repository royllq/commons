package net.wicp.tams.commons.exception;

/***
 * 如果需要有动态参数则用此配置
 * @author zhoujunhui
 *
 */
public interface IDynaMsg {
    public String packMsg(String msg,Object ctx);
}
