package chat;
import java.math.BigInteger;
import java.security.SecureRandom;

public class RSA {
	private final static BigInteger one = new BigInteger("1");
	private final static SecureRandom random = new SecureRandom();
	
	private BigInteger privateKey;
	private BigInteger publicKey;
	private BigInteger modulus;
	
	private BigInteger givenPublicKey;
	private BigInteger givenModulus;
	
	private boolean receivedPublic = false;
	private boolean receivedModulus = false;
	
	public boolean isReceivedPublic() {
		return receivedPublic;
	}

	public void setReceivedPublic(boolean receivedPublic) {
		this.receivedPublic = receivedPublic;
	}

	public boolean isReceivedModulus() {
		return receivedModulus;
	}

	public void setReceivedModulus(boolean receivedModulus) {
		this.receivedModulus = receivedModulus;
	}
	
	public BigInteger getPublicKey() {
		return publicKey;
	}

	public BigInteger getModulus() {
		return modulus;
	}
	
	public BigInteger getGivenPublicKey() {
		return givenPublicKey;
	}

	public void setGivenPublicKey(BigInteger givenPublicKey) {
		this.givenPublicKey = givenPublicKey;
	}

	public BigInteger getGivenModulus() {
		return givenModulus;
	}

	public void setGivenModulus(BigInteger givenModulus) {
		this.givenModulus = givenModulus;
	}

	public RSA (int n)
	{
		BigInteger p = BigInteger.probablePrime(n/2, random);
		BigInteger q = BigInteger.probablePrime(n/2, random);
		BigInteger phi = (p.subtract(one)).multiply(q.subtract(one));
		
		modulus = p.multiply(q);
		publicKey = new BigInteger("65537");
		privateKey = publicKey.modInverse(phi);
	}
	
	String encrypt (String msg)
	{
		BigInteger e = new BigInteger(msg.getBytes());
		e = e.modPow(getGivenPublicKey(), getGivenModulus());
		return e.toString();
	}
	
	BigInteger decrypt (String msg)
	{
		BigInteger d = new BigInteger(msg); //Already in bytes form!
		d = d.modPow(privateKey, modulus);
		return d;
	}
	
	public String toString()
	{
		String s = "";
		s += "public = " + publicKey + "\n";
		s += "private = " + privateKey + "\n";
		s +=  "modulus = " + modulus;
		return s;
	}
	
	public byte[] convertStringToASCII(String msg)
	{
		return msg.getBytes();
	}
	
	public String convertByteArrToString(byte[] bytes)
	{
		return new String(bytes);
	}
}
