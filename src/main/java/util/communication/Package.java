package util.communication;

/**
 * Created by Administrator on 2015/4/23.
 */
public class Package {

    public static final byte REG_MODE = 0;
    public static final byte REG_ROLL = 1;
    public static final byte REG_PITCH = 2;
    public static final byte REG_YAW = 3;
    public static final byte REG_THROTTLE = 4;

    public static void BuildPackage(byte[] bytes,byte reg ,byte data,int offset)
    {
        bytes[offset+0] = 'P';
        bytes[offset+1] = 'X';
        bytes[offset+2] =  0;
        bytes[offset+3] =  reg;
        bytes[offset+4] =  0;
        bytes[offset+5] =  0;
        bytes[offset+6] =  0;
        bytes[offset+7] =  data;
        //checksum
        bytes[offset+8] =  0;
        bytes[offset+9] =  'E';
        bytes[offset+10] =  '\0';
    }


}
