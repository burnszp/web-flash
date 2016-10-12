package cn.enilu.flash.core.util;


import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.Map;

/**
 * spring发送mail工具
 *
 * @author  enilu(eniluzt@qq.com)
 */
public class MailUtil {

    private static Logger logger = LoggerFactory.getLogger(MailUtil.class);

    private JavaMailSenderImpl javaMailSender;
    private VelocityEngine velocityEngine;//模板解析


    /**
     * @param mailBean
     * @return
     * @throws MessagingException
     */
    public boolean send(MailBean mailBean) throws MessagingException {
        MimeMessage msg = createMimeMessage(mailBean);
        if(msg == null){
            return false;
        }
        this.sendMail(msg, mailBean);
        return true;
    }

    public boolean send(String[] toMails, String subject, String content)throws MessagingException {
        MailBean mailBean = new MailBean();
        mailBean.setSubject(subject);
        mailBean.setToEmails(toMails);
        mailBean.setContent(content);
        send(mailBean);
        return true;
    }

    public boolean send(String[] toMails, String subject, String content, File[] attachments)throws MessagingException {
        MailBean mailBean = new MailBean();
        mailBean.setSubject(subject);
        mailBean.setToEmails(toMails);
        mailBean.setContent(content);
        mailBean.setAttachments(attachments);
        send(mailBean);
        return true;
    }

    public boolean send(String fromName, String[] toMails, String subject, String content, File[] attachments)throws MessagingException {
        MailBean mailBean = new MailBean();
        mailBean.setFromName(fromName);
        mailBean.setSubject(subject);
        mailBean.setToEmails(toMails);
        mailBean.setContent(content);
        mailBean.setAttachments(attachments);
        send(mailBean);
        return true;
    }

    private void sendMail(MimeMessage msg, MailBean mailBean){
        javaMailSender.send(msg);
        logger.info("$$$ Send mail Subject: {}, TO: {}", mailBean.getSubject(), arrayToStr(mailBean.getToEmails()));
    }

    /*
     * 记日志用的
     */
    private String arrayToStr(String[] array){
        if(array == null || array.length == 0){
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for(String str : array){
            sb.append(str+" , ") ;
        }
        return sb.toString();
    }

    /*
     * 根据 mailBean 创建 MimeMessage
     */
    private MimeMessage createMimeMessage(MailBean mailBean) throws MessagingException {
        if (!checkMailBean(mailBean)) {
            return null;
        }
        String text = getMessage(mailBean);
        if(text == null){
            logger.warn("@@@ warn mail text is null (Thread name="
                    + Thread.currentThread().getName() + ") @@@ " + mailBean.getSubject());
            return null;
        }

        MimeMessage msg = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(msg, true, "UTF-8");
        try {
            messageHelper.setFrom(javaMailSender.getUsername(), mailBean.getFromName());
        } catch (UnsupportedEncodingException e) {
            logger.error("set mail from error: ", e);
        }

        messageHelper.setSubject(mailBean.getSubject());
        messageHelper.setTo(mailBean.getToEmails());
        messageHelper.setText(text, true); // html: true

        for(File attachment : mailBean.getAttachments())
        {
            FileSystemResource attachmentFile = new FileSystemResource(attachment);
            messageHelper.addAttachment(attachment.getName(), attachmentFile);
        }

        return msg;
    }

    /*
     * 模板解析
     * @param mailBean
     * @return
     */
    private String getMessage(MailBean mailBean) {
        if(StringUtils.isNotEmpty(mailBean.getContent()))
        {
            return mailBean.getContent();
        }
        StringWriter writer = null;
        try {
            writer = new StringWriter();
            VelocityContext context = new VelocityContext(mailBean.getData());
            velocityEngine.evaluate(context, writer, "", mailBean.getTemplate());
            return writer.toString();
        } catch (VelocityException e) {
            logger.error(" VelocityException : " + mailBean.getSubject() + "\n" + e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    logger.error("###StringWriter close error ... ");
                }
            }
        }
        return null;
    }

    /*
     * check 邮件
     */
    private boolean checkMailBean(MailBean mailBean){
        if (mailBean == null) {
            logger.warn("@@@ warn mailBean is null (Thread name="
                    + Thread.currentThread().getName() + ") ");
            return false;
        }
        if (mailBean.getSubject() == null) {
            logger.warn("@@@ warn mailBean.getSubject() is null (Thread name="
                    + Thread.currentThread().getName() + ") ");
            return false;
        }
        if (mailBean.getToEmails() == null) {
            logger.warn("@@@ warn mailBean.getToEmails() is null (Thread name="
                    + Thread.currentThread().getName() + ") ");
            return false;
        }
        return true;
    }


    /*===================== setter & getter =======================*/
    public void setJavaMailSender(JavaMailSenderImpl javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    public static class MailBean implements Serializable {
        private String fromName;
        private String[] toEmails;
        private String subject;
        private Map data ;//邮件数据
        private String template;//邮件模板
        private File[] attachments;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        private String content;

        public MailBean()
        {
            attachments = new File[]{};
        }

        public File[] getAttachments() {
            return attachments;
        }

        public void setAttachments(File[] attachments) {
            this.attachments = attachments;
        }

        public String getFromName() {
            return fromName;
        }

        public void setFromName(String fromName) {
            this.fromName = fromName;
        }

        public String[] getToEmails() {
            return toEmails;
        }

        public void setToEmails(String[] toEmails) {
            this.toEmails = toEmails;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public Map getData() {
            return data;
        }

        public void setData(Map data) {
            this.data = data;
        }

        public String getTemplate() {
            return template;
        }

        public void setTemplate(String template) {
            this.template = template;
        }
    }

}
