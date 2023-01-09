import base64
import binascii
from Crypto.Cipher import AES


# 数据类
class MData():
    def __init__(self, data=b"", characterSet='utf-8'):
        # data肯定为bytes
        self.data = data
        self.characterSet = characterSet

    def saveData(self, FileName):
        with open(FileName, 'wb') as f:
            f.write(self.data)

    def fromString(self, data):
        self.data = data.encode(self.characterSet)
        return self.data

    def fromBase64(self, data):
        self.data = base64.b64decode(data.encode(self.characterSet))
        return self.data

    def fromHexStr(self, data):
        self.data = binascii.a2b_hex(data)
        return self.data

    def toString(self):
        return self.data.decode(self.characterSet)

    def toBase64(self):
        return base64.b64encode(self.data).decode()  # decode()转成字符串形式，否则为字节型数据

    def toHexStr(self):
        return binascii.b2a_hex(self.data).decode()

    def toBytes(self):
        return self.data

    def __str__(self):
        try:
            return self.toString()
        except Exception:
            return self.toBase64()


# 封装类
class AesCryptor:
    def __init__(self, key, iv='', padding_mode="NoPadding"):
        """
        构建一个AES对象
        key: 秘钥，字节型数据
        mode: 使用模式，AES.MODE_CBC
        iv： iv偏移量，字节型数据
        paddingMode: 填充模式，默认为NoPadding, 可选NoPadding，ZeroPadding，PKCS5Padding，PKCS7Padding
        characterSet: 字符集编码
        """
        self.key = key
        self.mode = AES.MODE_CBC
        self.iv = iv
        self.characterSet = "utf-8"
        self.paddingMode = padding_mode
        self.data = ""

    def __zero_padding(self, data):
        data += b'\x00'
        while len(data) % 16 != 0:
            data += b'\x00'
        return data

    def __strip_zero_padding(self, data):
        data = data[:-1]
        while len(data) % 16 != 0:
            data = data.rstrip(b'\x00')
            if data[-1] != b"\x00":
                break
        return data

    def __PKCS5_7Padding(self, data):
        needSize = 16 - len(data) % 16
        if needSize == 0:
            needSize = 16
        return data + needSize.to_bytes(1, 'little') * needSize

    def __StripPKCS5_7Padding(self, data):
        paddingSize = data[-1]
        return data.rstrip(paddingSize.to_bytes(1, 'little'))

    def __paddingData(self, data):
        if self.paddingMode == "NoPadding":
            if len(data) % 16 == 0:
                return data
            else:
                return self.__zero_padding(data)
        elif self.paddingMode == "ZeroPadding":
            return self.__zero_padding(data)
        elif self.paddingMode == "PKCS5Padding" or self.paddingMode == "PKCS7Padding":
            return self.__PKCS5_7Padding(data)
        else:
            print("不支持Padding")

    def __stripPaddingData(self, data):
        if self.paddingMode == "NoPadding":
            return self.__strip_zero_padding(data)
        elif self.paddingMode == "ZeroPadding":
            return self.__strip_zero_padding(data)

        elif self.paddingMode == "PKCS5Padding" or self.paddingMode == "PKCS7Padding":
            return self.__StripPKCS5_7Padding(data)
        else:
            print("不支持Padding")

    def setCharacterSet(self, characterSet):
        '''
        设置字符集编码
        characterSet: 字符集编码
        '''
        self.characterSet = characterSet

    def setPaddingMode(self, mode):
        '''
        设置填充模式
        mode: 可选NoPadding，ZeroPadding，PKCS5Padding，PKCS7Padding
        '''
        self.paddingMode = mode

    def decryptFromBase64(self, entext):
        '''
        从base64编码字符串编码进行AES解密
        entext: 数据类型str
        '''
        mData = MData(characterSet=self.characterSet)
        self.data = mData.fromBase64(entext)
        return self.__decrypt()

    def decryptFromHexStr(self, entext):
        '''
        从hexstr编码字符串编码进行AES解密
        entext: 数据类型str
        '''
        mData = MData(characterSet=self.characterSet)
        self.data = mData.fromHexStr(entext)
        return self.__decrypt()

    def decryptFromString(self, entext):
        '''
        从字符串进行AES解密
        entext: 数据类型str
        '''
        mData = MData(characterSet=self.characterSet)
        self.data = mData.fromString(entext)
        return self.__decrypt()

    def decryptFromBytes(self, entext):
        """
        从二进制进行AES解密
        entext: 数据类型bytes
        """
        self.data = entext
        return self.__decrypt()

    def encryptFromString(self, data):
        """
        对字符串进行AES加密
        data: 待加密字符串，数据类型为str
        """
        self.data = data.encode(self.characterSet)
        return self.__encrypt()

    def __encrypt(self):
        aes = AES.new(self.key.encode("utf8"), self.mode, self.iv.encode("utf8"))
        data = self.__paddingData(self.data)
        enData = aes.encrypt(data)
        return MData(enData)

    def __decrypt(self):
        aes = AES.new(self.key, self.mode, self.iv)
        data = aes.decrypt(self.data)
        # mData = MData(self.__stripPaddingData(data),characterSet=self.characterSet)
        # return mData()
        return MData(self.__stripPaddingData(data), characterSet=self.characterSet)


if __name__ == '__main__':
    key = b"1234567812345678"
    iv = b"0000000000000000"
    aes = AesCryptor(key, AES.MODE_CBC, iv, padding_mode="ZeroPadding", character_set='utf-8')

    data = "好好学习"
    rData = aes.encryptFromString(data)
    print("密文：", rData.toBase64())
    rData = aes.decryptFromBase64(rData.toBase64())
    print("明文：", rData)
# ————————————————
# 版权声明：本文为CSDN博主「Hello_wshuo」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
# 原文链接：https://blog.csdn.net/chouzhou9701/article/details/122019967
