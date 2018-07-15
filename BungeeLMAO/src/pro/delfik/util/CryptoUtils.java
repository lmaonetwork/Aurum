package pro.delfik.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class CryptoUtils {
	private static final String algoritm = "Blowfish";
	private final SecretKeySpec key;
	private static final Keccak hash = new Keccak();
	
	public CryptoUtils(String key) {
		this.key = new SecretKeySpec(key.getBytes(), algoritm);
	}
	
	public String encrypt(String data) {
		try {
			Cipher c = Cipher.getInstance(algoritm);
			c.init(1, this.key);
			byte[] encVal = c.doFinal(data.getBytes());
			return new String(Base64.getEncoder().encode(encVal));
		} catch (Exception var4) {
			var4.printStackTrace();
			return null;
		}
	}
	
	public String decrypt(String encryptedData) {
		try {
			Cipher c = Cipher.getInstance(algoritm);
			c.init(2, this.key);
			byte[] decodedValue = Base64.getDecoder().decode(encryptedData);
			byte[] decValue = c.doFinal(decodedValue);
			return new String(decValue);
		} catch (Exception var5) {
			var5.printStackTrace();
			return null;
		}
	}
	
	public static String md5Custom(String st) {
		byte[] digest = new byte[0];
		
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(st.getBytes());
			digest = messageDigest.digest();
		} catch (NoSuchAlgorithmException var5) {
			var5.printStackTrace();
		}
		
		BigInteger bigInt = new BigInteger(1, digest);
		
		String md5Hex;
		for (md5Hex = bigInt.toString(16); md5Hex.length() < 32; md5Hex = "0" + md5Hex) {
			;
		}
		
		return md5Hex;
	}
	
	private static long toLong(String message) {
		long result = 1L;
		
		for (int i = 0; i < message.length(); ++i) {
			char c = message.charAt(i);
			if (i % 2 == 0) {
				result += (long) c;
			} else {
				result *= (long) c;
			}
		}
		
		return result;
	}
	
	public static String getHash(String message) {
		message = toLong(message) + "";
		return hash.getHash(message, Keccak.Parameters.SHA3_512);
	}
	
	public static class Keccak {
		public static final int DEFAULT_PERMUTATION_WIDTH = 1600;
		private static BigInteger BIT_64 = new BigInteger("18446744073709551615");
		private BigInteger[] RC = new BigInteger[]{new BigInteger("0000000000000001", 16), new BigInteger("0000000000008082", 16), new BigInteger("800000000000808A", 16), new BigInteger("8000000080008000", 16), new BigInteger("000000000000808B", 16), new BigInteger("0000000080000001", 16), new BigInteger("8000000080008081", 16), new BigInteger("8000000000008009", 16), new BigInteger("000000000000008A", 16), new BigInteger("0000000000000088", 16), new BigInteger("0000000080008009", 16), new BigInteger("000000008000000A", 16), new BigInteger("000000008000808B", 16), new BigInteger("800000000000008B", 16), new BigInteger("8000000000008089", 16), new BigInteger("8000000000008003", 16), new BigInteger("8000000000008002", 16), new BigInteger("8000000000000080", 16), new BigInteger("000000000000800A", 16), new BigInteger("800000008000000A", 16), new BigInteger("8000000080008081", 16), new BigInteger("8000000000008080", 16), new BigInteger("0000000080000001", 16), new BigInteger("8000000080008008", 16)};
		private int[][] r = new int[][]{{0, 36, 3, 41, 18}, {1, 44, 10, 45, 2}, {62, 6, 43, 15, 61}, {28, 55, 25, 21, 56}, {27, 20, 39, 8, 14}};
		private int w;
		private int n;
		
		public Keccak() {
			this.initialize(1600);
		}
		
		public Keccak(int b) {
			this.initialize(b);
		}
		
		public String getHash(String message, Keccak.Parameters parameters) {
			BigInteger[][] S = new BigInteger[5][5];
			
			for (int i = 0; i < 5; ++i) {
				for (int j = 0; j < 5; ++j) {
					S[i][j] = new BigInteger("0", 16);
				}
			}
			
			BigInteger[][] P = this.padding(message, parameters);
			for (BigInteger[] Pi : P) {
				for (int i = 0; i < 5; ++i) {
					for (int j = 0; j < 5; ++j) {
						if (i + j * 5 < parameters.getR() / this.w) {
							S[i][j] = S[i][j].xor(Pi[i + j * 5]);
						}
					}
				}
				this.doKeccackf(S);
			}
			
			String Z = "";
			
			do {
				for (int i = 0; i < 5; ++i) {
					for (int j = 0; j < 5; ++j) {
						if (5 * i + j < parameters.getR() / this.w) {
							Z = Z + this.addZero(Keccak.HexUtils.getReverseHex(S[j][i].toByteArray()), 16).substring(0, 16);
						}
					}
				}
				
				this.doKeccackf(S);
			} while (Z.length() < parameters.getOutputLength() * 2);
			
			return Z.substring(0, parameters.getOutputLength() * 2);
		}
		
		private BigInteger[][] doKeccackf(BigInteger[][] A) {
			for (int i = 0; i < this.n; ++i) {
				A = this.roundB(A, this.RC[i]);
			}
			
			return A;
		}
		
		private BigInteger[][] roundB(BigInteger[][] A, BigInteger RC) {
			BigInteger[] C = new BigInteger[5];
			BigInteger[] D = new BigInteger[5];
			BigInteger[][] B = new BigInteger[5][5];
			
			int i;
			for (i = 0; i < 5; ++i) {
				C[i] = A[i][0].xor(A[i][1]).xor(A[i][2]).xor(A[i][3]).xor(A[i][4]);
			}
			
			for (i = 0; i < 5; ++i) {
				D[i] = C[(i + 4) % 5].xor(this.rot(C[(i + 1) % 5], 1));
			}
			
			int j;
			for (i = 0; i < 5; ++i) {
				for (j = 0; j < 5; ++j) {
					A[i][j] = A[i][j].xor(D[i]);
				}
			}
			
			for (i = 0; i < 5; ++i) {
				for (j = 0; j < 5; ++j) {
					B[j][(2 * i + 3 * j) % 5] = this.rot(A[i][j], this.r[i][j]);
				}
			}
			
			for (i = 0; i < 5; ++i) {
				for (j = 0; j < 5; ++j) {
					A[i][j] = B[i][j].xor(B[(i + 1) % 5][j].not().and(B[(i + 2) % 5][j]));
				}
			}
			
			A[0][0] = A[0][0].xor(RC);
			return A;
		}
		
		private BigInteger rot(BigInteger x, int n) {
			n %= this.w;
			BigInteger leftShift = this.getShiftLeft64(x, n);
			BigInteger rightShift = x.shiftRight(this.w - n);
			return leftShift.or(rightShift);
		}
		
		private BigInteger getShiftLeft64(BigInteger value, int shift) {
			BigInteger retValue = value.shiftLeft(shift);
			BigInteger tmpValue = value.shiftLeft(shift);
			if (retValue.compareTo(BIT_64) > 0) {
				for (int i = 64; i < 64 + shift; ++i) {
					tmpValue = tmpValue.clearBit(i);
				}
				
				tmpValue = tmpValue.setBit(64 + shift);
				retValue = tmpValue.and(retValue);
			}
			
			return retValue;
		}
		
		private BigInteger[][] padding(String message, Keccak.Parameters parameters) {
			for (message = message + parameters.getD(); message.length() / 2 * 8 % parameters.getR() != parameters.getR() - 8; message = message + "00") {
				;
			}
			
			message = message + "80";
			int size = message.length() / 2 * 8 / parameters.getR();
			BigInteger[][] arrayM = new BigInteger[size][];
			arrayM[0] = new BigInteger[1600 / this.w];
			this.initArray(arrayM[0]);
			int count = 0;
			int j = 0;
			int i = 0;
			
			for (int _n = 0; _n < message.length(); ++_n) {
				if (j > parameters.getR() / this.w - 1) {
					j = 0;
					++i;
					if (arrayM.length == i) {
						continue;
					}
					
					arrayM[i] = new BigInteger[1600 / this.w];
					this.initArray(arrayM[i]);
				}
				
				++count;
				if (count * 4 % this.w == 0) {
					String subString = message.substring(count - this.w / 4, this.w / 4 + (count - this.w / 4));
					arrayM[i][j] = new BigInteger(subString, 16);
					String revertString = Keccak.HexUtils.getReverseHex(arrayM[i][j].toByteArray());
					revertString = this.addZero(revertString, subString.length());
					arrayM[i][j] = new BigInteger(revertString, 16);
					++j;
				}
			}
			
			return arrayM;
		}
		
		private String addZero(String str, int length) {
			String retStr = str;
			
			for (int i = 0; i < length - str.length(); ++i) {
				retStr = retStr + "0";
			}
			
			return retStr;
		}
		private void initArray(BigInteger[] array) {
			for (int i = 0; i < array.length; ++i) {
				array[i] = new BigInteger("0", 16);
			}
			
		}
		
		private void initialize(int b) {
			this.w = b / 25;
			int l = (int) (Math.log((double) this.w) / Math.log(2.0D));
			this.n = 12 + 2 * l;
		}
		
		public static class HexUtils {
			private static final char[] DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
			
			public HexUtils() {
			}
			
			public static String getHex(byte[] data) {
				int l = data.length;
				char[] outData = new char[l << 1];
				int i = 0;
				
				for (int var4 = 0; i < l; ++i) {
					outData[var4++] = DIGITS[(240 & data[i]) >>> 4];
					outData[var4++] = DIGITS[15 & data[i]];
				}
				
				return new String(outData);
			}
			
			public static String getReverseHex(byte[] data) {
				return getHex(reverse(data));
			}
			
			private static byte[] reverse(byte[] data) {
				int i = 0;
				
				for (int j = data.length - 1; j > i; ++i) {
					byte tmp = data[j];
					data[j] = data[i];
					data[i] = tmp;
					--j;
				}
				
				return data;
			}
		}
		
		public static enum Parameters {
			KECCAK_224(1152, 28, "01"),
			KECCAK_256(1088, 32, "01"),
			KECCAK_384(832, 48, "01"),
			KECCAK_512(576, 64, "01"),
			SHA3_224(1152, 28, "06"),
			SHA3_256(1088, 32, "06"),
			SHA3_384(832, 48, "06"),
			SHA3_512(576, 64, "06"),
			SHAKE128(1344, 32, "1F"),
			SHAKE256(1088, 64, "1F");
			
			private final int r;
			private final int outputLength;
			private final String d;
			
			private Parameters(int r, int outputLength, String d) {
				this.r = r;
				this.outputLength = outputLength;
				this.d = d;
			}
			
			public int getR() {
				return this.r;
			}
			
			public int getOutputLength() {
				return this.outputLength;
			}
			
			public String getD() {
				return this.d;
			}
		}
	}
}
