package cn.dqb.qiniutool.task;

/**
 * @date 2019/10/18 15:13
 */
public interface IQiniuBaseTask {

    String generateRule() throws Exception;

    String execute() throws Exception;
}
