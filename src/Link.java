
public class Link {
	public static enum BAND_TYPE {
		KBPS(1, "Kpbs", 0), MBPS(1000, "Mbps", 1), GPBS(1000000, "Gbps", 2), TBPS(1000000000, "Tbps", 3);
		
		public final static int NUM_INTERNAL = 3;
		private int bitNum;
		private String name;
		private int internal;
		
		BAND_TYPE(int bitNum, String name, int internal){
			this.bitNum = bitNum;
			this.name = name;
			this.internal = internal;
		}
		
		public static BAND_TYPE valueOf(int value) {
		    for (BAND_TYPE type : values()) {
		        if (type.internal == value) {
		            return type;
		        }
		    }    
		    throw new IllegalArgumentException(String.valueOf(value));
		}
		
		int getInternal(){
			return internal;
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
	
	public int getBandwidthValue(){
		return bandwidthValue;
	}
	public BAND_TYPE getBandwidthType(){
		return bandwidthType;
	}
	
	public String toString(){
		return bandwidthValue + " " + bandwidthType.getName();
	}
}
