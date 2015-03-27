
public class Link {
	public static enum BAND_TYPE {
		KBPS(1, "Kpbs"), MBPS(1000, "Mbps"), GPBS(1000000, "Gbps"), TBPS(1000000000, "Tbps");
		
		private int bitNum;
		private String name;
		
		BAND_TYPE(int bitNum, String name){
			this.bitNum = bitNum;
			this.name = name;
		}
		
		int getBitNum(){
			return bitNum;
		}
		
		String getName(){
			return name;
		}
	};
	
	private int bandwidthValue;
	private BAND_TYPE bandwidthType;
	
	public Link(int bandwidth, BAND_TYPE type){
		bandwidthValue = bandwidth;
		bandwidthType = type;
	}
	
	public String toString(){
		return bandwidthValue + " " + bandwidthType.getName();
	}
}
