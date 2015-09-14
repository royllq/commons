package net.wicp.tams.commons.connector.config.xmlParser;

/**
 * 配置文件
 * */
public abstract class XMLNameSpace {
	public static final String inputRoot = "PropertyIn";// 输入参数的根结点
	public static final String outputRoot = "PropertyOut";// 输出参数的根结点
	public static final String InterFaceMapping = "InterFaceMapping";// 整个文档的根
	public static final String ControlInfo = "ControlInfo";// 控制信息
	

	public static final String col = "COL";

	public static final String namespacestart = "xmlns:";
	public static final String namespaceSplit = ":";
	public static final char elementstartTag = '<';
	public static final String soapRefKey = "href";
	public static final char lastelementEndTag = '/';
	public static final String elementendTag = ">";
	public static final String soapBodyname = "SOAP-ENV:Body";
	public static final String soapEnvelopeName = "SOAP-ENV:Envelope";
	public static final String soapEnvelopeAtts = "xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"";
	public static final String errorSoapMessageModle = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><soapenv:Body>		<soapenv:Fault>	<faultcode> !faultcode! </faultcode><faultstring> !faultstring! </faultstring><detail> !detail!	</detail></soapenv:Fault></soapenv:Body></soapenv:Envelope>";
	public static final String xmldes = "<?xml version=\"1.0\" encoding=\"";
	public static final String xmldes1 = "\" ?>";
	public static final String soapcfg_of_nameSpace = "namespace";
	public static final String soapcfg_of_nskey = "nskey";
	public static final String ibss_xml_heaer = "<?xml version=\"1.0\" encoding=\"gb2312\" standalone=\"yes\" ?>";
}