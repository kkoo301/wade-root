package test.common.string;

import com.ailk.common.util.BASE64Util;

/**
 * 转换Byte为HexString数组
 * @author Shieh
 *
 */
public class Byte2HexString 
{
	
    public static void main(String[] args) {
    	
    	String privKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCqDGLO0hs6YmJ6CXaKaHOxii8EcwAcWyPlVGtEW+SOed2UZV/QD5tMdLIS35qG6ufATQBr7ehFwxxtSxaWWOaze6PlEHvnk7yBX/Ey09QdRPez8t4SRsFEcIM6eA47Rbcu6WlqU6v1eq97XmkToNmaOQLpb3bhpHnm/X/th1scFwIDAQAB";
    	byte[] keyByte = BASE64Util.decode(privKey);
		
		/*
        String s = "00000018A0010098C68E00989A690000000000BC614E000055AA55AA";
        System.out.println(s);
        byte[] b = HexString2Bytes(s);
        System.out.println(Bytes2HexString(b));
        
        */
		
		System.out.println( Bytes2HexString(keyByte) );
    }
    

    /**
     * 将指定byte数组以16进制的形式打印到控制台
     * 
     * @param hint
     *            String
     * @param b
     *            byte[]
     * @return void
     */
    public static void printHexString(String hint, byte[] b) {
        System.out.print(hint);
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            System.out.print(hex.toUpperCase() + " ");
        }
        System.out.println("");
    }

    /**
     * 
     * @param b
     *            byte[]
     * @return String
     */
    public static String Bytes2HexString(byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += " 0x" + hex.toUpperCase();
        }
        return ret;
    }

    /**
     * 将两个ASCII字符合成一个字节； 如："EF"–> 0xEF
     * 
     * @param src0
     *            byte
     * @param src1
     *            byte
     * @return byte
     */
    public static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[] {src0})).byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 })).byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    /**
     * 将指定字符串src，以每两个字符分割转换为16进制形式 如："2B44EFD9" –> byte[]{0x2B, 0×44, 0xEF,
     * 0xD9}
     * 
     * @param src
     *            String
     * @return byte[]
     */
    public static byte[] HexString2Bytes(String src) {
        if (null == src || 0 == src.length()) {
            return null;
        }
        byte[] ret = new byte[src.length() / 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < (tmp.length / 2); i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

}