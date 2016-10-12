package cn.enilu.flash.web.taglib;

import javax.servlet.jsp.JspWriter;
import java.io.IOException;

/**
 * css标签
 *
 *  @author  enilu(einluzt@qq.com)
 */
public class LinkTag extends AssetAwareTag {

    private String id;

    @Override
    protected void createTag(JspWriter out, String finalPath) throws IOException {
        StringBuilder ss = new StringBuilder("<link");
        if (id != null) {
            ss.append(" id=\"").append(id).append("\"");
        }
        ss.append(" rel=\"stylesheet\" type=\"text/css\" charset=\"UTF-8\" href=\"").append(finalPath).append("\"");
        fillDynamicAttributes(ss);
        ss.append(">");
        out.write(ss.toString());
    }

    public void setId(String id) {
        this.id = id;
    }
}
