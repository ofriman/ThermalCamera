package com.example.utility;

import java.util.Arrays;


public class RawCompression {

	private final static short _ZERO_BIT = 1 ;
	private final static short _ONE_BIT = 2 ;
	private final static short _TWO_BITS = 4 ;
	private final static short _THREE_BITS = 8 ;
	private final static short _FOUR_BITS = 16 ;
	private final static short _FIVE_BITS = 32;
	private final static short _SIX_BITS = 64 ;
	private final static short _SEVEN_BITS = 128 ;
	private final static short _EIGHT_BITS = 256 ;
	private final static short _NINE_BITS = 512 ;
	private final static short _TEN_BITS = 1024 ;
	private final static short _ELEVEN_BITS = 2048 ;
	private final static short _TWELVE_BITS = 4096 ;
	private final static short _THIRTEEN_BITS = 8196 ;
	private final static short _FOURTEEN_BITS = 16384 ;

	private final static short _TYPE_SIZE = _FOUR_BITS ;
	private final static short _MAX_BITS = _FOURTEEN_BITS ;
	//final static int _ARRAY_SIZE = 110592 ; // THERM APP MAX PIXEL  (2^17 bit) 
	private final static int _MAX_ARRAY_SIZE = 4194304  ; // MAX INPUT ARRAY  (5 bit garbage value ... room to improvement)
	private final static int _HEADER_SIZE = 5 ;
	private final static int _MIN_INDEX = 0 ;
	private final static int _SIZE_INDEX = 1 ;

	private static byte[] HeaderInjection(byte[] arr,byte type,int min,int size) {

		if ( arr == null || arr.length < _HEADER_SIZE)
			return null;
		if ( type < 0 || _TYPE_SIZE <= type)
			return null ;
		if	(min < 0 || _MAX_BITS <= min)
			return null ;
		if(size < 0 || _MAX_ARRAY_SIZE <= size)
			return null ;

		arr[0] = (byte)( (type &0xF )|((min&0xF)<<4));
		arr[1] = (byte)( ((min &0xFF0)>>4));
		arr[2] = (byte)( ((min &0x3000)>>12)|((size&0x3F)<<2)	); 
		arr[3] = (byte) ((size&0x3FC0)>>>6);
		arr[4] = (byte) ((size&0x3FC000)>>>14);

		return arr ;
	}
	private static int[] HeaderInterpreter(byte[] arr) {

		if ( arr == null || arr.length < _HEADER_SIZE)
			return null;
		int min =  (((arr[0]&0xF0)>>4)|((arr[1]&0xFF)<<4)| ((arr[2]&0x3)<<12) );
		int size = (((arr[2]&0xFC)>>2)|((arr[3]&0xFF)<<6)|((arr[4]&0xFF)<<14) ); 
		int ans [] = {min,size};
		return ans ;
	}

	public static int[] Decompress(byte [] arr)
	{
		// CHEACK FOR WRONG INPUT
		if(arr == null || arr.length < _HEADER_SIZE )
			return null;

		byte compression_type =(byte)(arr[0]&0xF) ;


		switch(compression_type){

		case 0 :
			return ZERODecompress(arr);
		case 1 :
			return ONEDecompress(arr);
		case 2 :
			return TWODecompress(arr);
		case 3 :
			return THREEDecompress(arr);
		case 4 :
			return FOURDecompress(arr);
		case 5 :
			return FIVEDecompress(arr);
		case 6 :
			return SIXDecompress(arr);
		case 7 :
			return SEVENDecompress(arr);
		case 8 :
			return EIGHTDecompress(arr);
		case 9 :
			return NINEDecompress(arr);
		case 10 : 
			return TENDecompress(arr);
		case 11 :
			return ELEVENDecompress(arr);
		case 12 :
			return TWELEFDecompress(arr);
		case 13 :
			return THIRTEENDecompress(arr);
		case 14 :
			return FOURTEENDecompress(arr);
		case 15 :
		case 16 :
		default :
			return null ; // Error , not in use.

		}
	}
	public static byte[] Compress(int [] arr)
	{
		// CHEACK FOR WRONG INPUT
		if(arr == null)
			return null;

		int size = arr.length ;

		if(size < 1 ||  _MAX_ARRAY_SIZE <= size)
			return null ;

		int temp_arr [] = new int[size];
		// DISCOVER MIN / MAX AND CLONE ARR
		int min =arr[0] , max = arr[0];
		for(int i = 0 ; i<size ;i++)
		{
			temp_arr[i]=arr[i];
			if (arr[i] > max) max = arr[i];
			else if (arr[i] < min)min = arr[i];
		}

		if(max >= _MAX_BITS)
			return null;  // MORE THEN MAX VALUE 
		if(min < 0)
			return null;  // MORE THEN MAX VALUE 
		

		//SUBTRACT MIN FROM ALL INDEXS 
		for(int i = 0 ; i<size ;i++)
		{
			temp_arr[i]-=min;
		}
		//COMPRESS TYPE - MAX BIT PER NUMBER
		short max_numbers =(short)( max - min + 1);

		if(max_numbers<=_ZERO_BIT)	
			return ZEROCompress(size,min);
		else if (max_numbers<=_ONE_BIT)
			return ONECompress(temp_arr,min);
		else if (max_numbers<=_TWO_BITS)
			return TWOCompress(temp_arr,min);
		else if (max_numbers<=_THREE_BITS)
			return THREECompress(temp_arr,min);
		else if(max_numbers<=_FOUR_BITS)
			return FOURCompress(temp_arr,min);
		else if(max_numbers<=_FIVE_BITS)
			return FIVECompress(temp_arr,min);
		else if(max_numbers<=_SIX_BITS)
			return SIXCompress(temp_arr,min);
		else if(max_numbers<=_SEVEN_BITS)
			return SEVENCompress(temp_arr,min);
		else if(max_numbers<=_EIGHT_BITS)
			return EIGHTCompress(temp_arr,min);
		else if(max_numbers<=_NINE_BITS)
			return NINECompress(temp_arr,min);
		else if(max_numbers<=_TEN_BITS)
			return TENCompress(temp_arr,min);
		else if(max_numbers<=_ELEVEN_BITS)
			return ELEVENCompress(temp_arr,min);
		else if(max_numbers<=_TWELVE_BITS)
			return TWELEFCompress(temp_arr,min);
		else if(max_numbers<=_THIRTEEN_BITS)
			return THIRTEENCompress(temp_arr,min);
		else if(max_numbers<=_FOURTEEN_BITS)
			return FOURTEENCompress(temp_arr,min);
		else	//UNKNOWN EROR
			return null;

	}

	private static byte[] ZEROCompress(int size,int min)
	{
		System.out.print("0");
		if(size < 1 | _MAX_ARRAY_SIZE <= size)
			return null ;

		byte [] ans = new byte[_HEADER_SIZE];
		final  byte  _TYPE = 0;
		return HeaderInjection(ans, _TYPE, min, size);
	}	
	private static int[] ZERODecompress(byte [] arr)
	{
		System.out.println("0");
		if(arr == null || arr.length < _HEADER_SIZE )
			return null;

		int header [] = HeaderInterpreter(arr);

		if(header == null || header.length != 2 )
			return null;

		int min = header[_MIN_INDEX];
		int size = header[_SIZE_INDEX];

		int ans [] = new int[size];
		Arrays.fill(ans , min);
		return ans ;
	}
	private static boolean ZEROTest() {
		for (int i = 0; i < _MAX_BITS; i++) {
			for (int j = 1; j <_MAX_ARRAY_SIZE; j++) {
				int [] a = new int [j];
				Arrays.fill(a, i);
				if (!Arrays.equals( Decompress(Compress(a)), a)){
					return false ;
				}
			}
		}
		return true ;
	}

	private static byte[] ONECompress(int [] arr , int min)
	{
		System.out.print("1");
		if(arr == null)
			return null ;

		int size = arr.length;

		if(size < 2 | _MAX_ARRAY_SIZE <= size)
			return null ;

		final  byte _TYPE = 1;

		int ans_size = _HEADER_SIZE +(int)( Math.ceil( (size*_TYPE)/8.0) );
		byte [] ans = new byte[ans_size];
		ans = HeaderInjection(ans, _TYPE, min, size);

		int i = _HEADER_SIZE ;

		for (int j=0,u=0; i < ans_size & j <size;u++) {
			if(u%8==0){
				ans[i] =(byte)((arr[j++] &0x1) << 0);
			}
			else if(u%8==1){
				ans[i] =(byte)(ans[i] | ((arr[j++] &0x1)<< 1));
			}
			else if(u%8==2){
				ans[i] =(byte)(ans[i] | ((arr[j++] &0x1) << 2));
			}
			else if(u%8==3){
				ans[i] =(byte)(ans[i] | ((arr[j++]  &0x1) << 3));
			}
			else if(u%8==4){
				ans[i] =(byte)(ans[i] |((arr[j++] &0x1)<< 4)) ;
			}
			else if(u%8==5){
				ans[i] =(byte)(ans[i] |((arr[j++] &0x1) <<5));
			}
			else if(u%8==6){
				ans[i] =(byte)(ans[i] |((arr[j++] &0x1) << 6));
			}
			else
			{
				ans[i] =(byte)(ans[i] |((arr[j++] &0x1)<< 7));
				i++;
			}
		}
		while(++i<ans_size)
			ans[i]=(byte)0;

		return ans;
	}
	private static int[] ONEDecompress(byte [] arr)
	{
		System.out.println("1");
		if(arr == null || arr.length <= _HEADER_SIZE )
			return null;

		int size = arr.length;

		int header [] = HeaderInterpreter(arr);

		if(header == null || header.length != 2 )
			return null;

		int min = header[_MIN_INDEX];
		int ans_size = header[_SIZE_INDEX];
		int ans [] = new int[ans_size];
		int i =0;
		for (int j=_HEADER_SIZE; i<ans_size&j<size ;) {
			if(i%8==0){
				ans[i++] = ((arr[j] & 0x1) >>> 0)+min ;
			}
			else if(i%8==1){
				ans[i++] = ((arr[j] & 0x2) >>> 1)+min ;
			}
			else if(i%8==2){
				ans[i++] = ((arr[j] & 0x4 )>>> 2)+min ;
			}
			else if(i%8==3){
				ans[i++] = ((arr[j] & 0x8 )>>> 3)+min ;
			}
			else if(i%8==4){
				ans[i++] = ((arr[j] & 0x10) >>> 4)+min ;
			}
			else if(i%8==5){
				ans[i++] = ((arr[j] & 0x20) >>> 5)+min ;
			}
			else if(i%8==6){
				ans[i++] = ((arr[j] & 0x40) >>> 6)+min ;
			}
			else {
				ans[i++] = ((arr[j++] & 0x80) >>> 7)+min ;	
			}
		}
		while(i<ans_size)
			ans[i++]=(byte)0;
		return ans ;
	}
	private static boolean ONETest() {
		for (int i = 0; i < _MAX_BITS-1; i++) {
			for (int j = 1; j <_MAX_ARRAY_SIZE; j++) {
				int [] a = new int [j];
				Arrays.fill(a, i);
				for (int k = 0; k < a.length; k=k+2) {
					a[k]++ ;
				}
				if (!Arrays.equals( Decompress(Compress(a)), a)){
					return false ;
				}
			}
		}
		return true ;
	}

	private static byte[] TWOCompress(int [] arr , int min)
	{
		System.out.print("2");
		if(arr == null)
			return null ;

		int size = arr.length;

		if(size < 2 | _MAX_ARRAY_SIZE <= size)
			return null ;
		final byte _TYPE = 2 ;
		int ans_size = _HEADER_SIZE +(int)( Math.ceil( (size*_TYPE)/8.0) );
		byte [] ans = new byte[ans_size];
		ans = HeaderInjection(ans, _TYPE, min, size);
		int i = _HEADER_SIZE ;

		for (int j=0,u=0; i < ans_size & j <size;u++) {
			if(u%4==0)
				ans[i] =(byte)((arr[j++] &0x3) << 0);
			else if(u%4==1)
				ans[i] =(byte)(ans[i] | ((arr[j++] &0x3)<< 2));
			else if(u%4==2)
				ans[i] =(byte)(ans[i] |  ((arr[j++] &0x3) << 4) );
			else {
				ans[i] =(byte)(ans[i] | ((arr[j++]  &0x3) << 6));
				i++;
			}
		}

		while(++i<ans_size)
			ans[i]=(byte)0;

		return ans;
	}
	private static int[] TWODecompress(byte [] arr)
	{
		System.out.println("2");
		if(arr == null || arr.length <= _HEADER_SIZE )
			return null;

		int size = arr.length;

		int header [] = HeaderInterpreter(arr);

		if(header == null || header.length != 2 )
			return null;

		int min = header[_MIN_INDEX];
		int ans_size = header[_SIZE_INDEX];
		int ans [] = new int[ans_size];
		int i =0;
		for (int j=_HEADER_SIZE; i<ans_size&j<size ;) {
			if(i%4==0)
				ans[i++] = ((arr[j] & 0x3) >>> 0)+min ;
			else if(i%4==1)
				ans[i++] = ((arr[j] & 0xC) >>> 2)+min ;
			else if(i%4==2)
				ans[i++] = ((arr[j] & 0x30 )>>> 4)+min ;
			else
				ans[i++] = ((arr[j++] & 0xC0 )>>> 6)+min ;
		}
		while(i<ans_size)
			ans[i++]=(byte)0;
		return ans ;
	}
	private static boolean TWOTest() {
		for (int i = 0; i < _MAX_BITS; i++) {
			for (int j = 1; j <_MAX_ARRAY_SIZE; j++) {
				int [] a = new int [j];
				Arrays.fill(a, i);
				for (int k = 1; k < a.length; k=k+2) {
					a[k]+=3 ;
				}
				if (!Arrays.equals( Decompress(Compress(a)), a)){
					return false ;
				}
			}
		}
		return true ;
	}

	private static byte[] THREECompress(int [] arr , int min)
	{
		System.out.print("3");
		if(arr == null)
			return null ;

		int size = arr.length;

		if(size < 2 | _MAX_ARRAY_SIZE <= size)
			return null ;
		final  byte _TYPE = 3 ;
		int ans_size = _HEADER_SIZE +(int)( Math.ceil( (size*_TYPE)/8.0) );
		byte [] ans = new byte[ans_size];
		ans = HeaderInjection(ans, _TYPE, min, size);
		int i = _HEADER_SIZE ;
		for (int j=0,u=0; i < ans_size & j <size;u++) {
			if(u%8==0)
				ans[i] =(byte) (arr[j++]&0x7);
			else if(u%8==1)
				ans[i] =(byte)(ans[i] | ((arr[j++]&0x7)<<3));
			else if(u%8==2){
				ans[i] =(byte)(ans[i] | ((arr[j]&0x3)<<6 ) );
				i++;
				if(i<ans_size)
					ans[i] =(byte) ((arr[j++]&0x4)>>2);
			}
			else if(u%8==3)
				ans[i] =(byte)(ans[i] | ((arr[j++]&0x7)<<1) );
			else if(u%8==4)
				ans[i] =(byte)(ans[i] | ((arr[j++]&0x7)<<4) ) ;
			else if(u%8==5){
				ans[i] =(byte)(ans[i] |((arr[j]&0x1)<<7));
				i++;
				if(i<ans_size)
					ans[i] =(byte) ((arr[j++]&0x6)>>1) ;
			}
			else if(u%8==6)
				ans[i] =(byte)(ans[i] |((arr[j++]&0x7)<<2) );
			else
			{
				ans[i] =(byte)(ans[i] | ((arr[j++]&0x7)<<5));
				i++;
			}
		}
		while(++i<ans_size)
			ans[i]=(byte)0;

		return ans;
	}
	private static int[] THREEDecompress(byte [] arr)
	{
		System.out.println("3");
		if(arr == null || arr.length <= _HEADER_SIZE )
			return null;

		int size = arr.length;

		int header [] = HeaderInterpreter(arr);

		if(header == null || header.length != 2 )
			return null;

		int min = header[_MIN_INDEX];
		int ans_size = header[_SIZE_INDEX];
		int ans [] = new int[ans_size];
		int i =0;

		for (int j=_HEADER_SIZE; i<ans_size&j<size ;) {
			if(i%8==0)
				ans[i++] =((arr[j] & 0x7) >>> 0)+min ;
			else if(i%8==1)
				ans[i++] =((arr[j] & 0x38) >>> 3)+min;
			else if(i%8==2)
				if(j<size-1)
					ans[i++] =(((arr[j++] & 0xC0 )>>> 6) | ( (arr[j] & 0x1) << 2))+min;
				else if(i%8==3)
					ans[i++] =((arr[j] & 0xE )>>>1)+min ;
				else if(i%8==4)
					ans[i++] =((arr[j] & 0x70) >>> 4)+min ;
				else if(i%8==5)
					if(j<size-1)
						ans[i++] =((arr[j++] & 0x80) >>> 7 | ((arr[j] & 0x3) << 1))+min;
					else if(i%8==6)
						ans[i++] =((arr[j] & 0x1C) >>> 2)+min ;
					else 
						ans[i++] =((arr[j] & 0xE0) >>> 5)+min ;
		}
		while(i<ans_size)
			ans[i++]=(byte)0;
		return ans ;
	}
	private static boolean THREETest() {
		for (int i = 0; i < _MAX_BITS; i++) {
			for (int j = 3; j <_MAX_ARRAY_SIZE; j++) {
				int [] a = new int [j];
				Arrays.fill(a, i);

				for (int k = 1; k < a.length; k=k+2) {
					a[k]+=7 ;
				}
				if (!Arrays.equals( Decompress(Compress(a)), a)){
					return false ;
				}
			}
		}
		return true ;
	}

	private static byte[] FOURCompress(int [] arr , int min)
	{
		System.out.print("4");
		if(arr == null)
			return null ;

		int size = arr.length;

		if(size < 2 | _MAX_ARRAY_SIZE <= size)
			return null ;
		final  byte _TYPE = 4 ;
		int ans_size = _HEADER_SIZE +(int)( Math.ceil( (size*_TYPE)/8.0) );
		byte [] ans = new byte[ans_size];
		ans = HeaderInjection(ans, _TYPE, min, size);
		int i = _HEADER_SIZE ;

		for (int j=0,u=0; i < ans_size & j <size;u++) {
			if(u%2==0)
				ans[i] = (byte) (arr[j++]&0xF)  ;
			else{
				ans[i] = (byte) ( ans[i] | ((arr[j++]&0xF)<< 4) );
				i++;
			}
		}
		while(++i<ans_size)
			ans[i]=(byte)0;

		return ans;
	}
	private static int[] FOURDecompress(byte [] arr)
	{
		System.out.println("4");
		if(arr == null || arr.length <= _HEADER_SIZE )
			return null;

		int size = arr.length;

		int header [] = HeaderInterpreter(arr);

		if(header == null || header.length != 2 )
			return null;

		int min = header[_MIN_INDEX];
		int ans_size = header[_SIZE_INDEX];
		int ans [] = new int[ans_size];
		int i =0;

		for (int j=_HEADER_SIZE; i<ans_size&j<size ;) {
			if(i%2==0)
				ans[i++] =((arr[j] & 0xF) >>> 0)+min ;
			else 
				ans[i++] =((arr[j++] & 0xF0) >>> 4)+min ;
		}
		while(i<ans_size)
			ans[i++]=(byte)0;
		return ans ;
	}
	private static boolean FOURTest() {
		for (int i = 0; i < _MAX_BITS; i++) {
			for (int j = 3; j <_MAX_ARRAY_SIZE; j++) {
				int [] a = new int [j];
				Arrays.fill(a, i);
				for (int k = 1; k < a.length; k=k+2) {
					a[k]+=15 ;
				}

				if (!Arrays.equals( Decompress(Compress(a)), a)){
					return false ;
				}	
			}
		}
		return true ;
	}

	private static byte[] FIVECompress(int [] arr , int min)
	{
		System.out.print("5");
		if(arr == null)
			return null ;

		int size = arr.length;

		if(size < 2 | _MAX_ARRAY_SIZE <= size)
			return null ;
		final  byte _TYPE = 5 ;
		int ans_size = _HEADER_SIZE +(int)( Math.ceil( (size*_TYPE)/8.0) );
		byte [] ans = new byte[ans_size];
		ans = HeaderInjection(ans, _TYPE, min, size);
		int i = _HEADER_SIZE ;

		for (int j=0,u=0; i < ans_size & j <size;u++) {
			if(u%8==0)
			{
				ans[i] =(byte)(arr[j++]&0x1F) ;
			}
			else if(u%8==1){
				ans[i] =(byte)(ans[i] |((arr[j]&0x7)<< 5));
				ans[++i] =(byte)((arr[j++]&0x18)>>>3) ;
			}
			else if(u%8==2){
				ans[i] =(byte)(ans[i] | ((arr[j++]&0x1F)<< 2));
			}
			else if(u%8==3){
				ans[i] =(byte)(ans[i] |((arr[j]&0x1)<< 7));
				ans[++i] =(byte)((arr[j++]&0x1E)>>>1) ;
			}
			else if(u%8==4){
				ans[i] =(byte)(ans[i] |((arr[j]&0xF)<< 4)) ;
				ans[++i] =(byte)((arr[j++]&0x10)>>>4) ;
			}
			else if(u%8==5){
				ans[i] =(byte)(ans[i] |((arr[j++]&0x1F)<< 1));
			}
			else if(u%8==6){
				ans[i] =(byte)(ans[i] |((arr[j]&0x3)<< 6));
				ans[++i] =(byte) ((arr[j++]&0x1C)>>>2) ;
			}
			else
			{
				ans[i++] =(byte)((arr[j++]&0x1F)<< 3) ;			
			}
		}
		while(++i<ans_size)
			ans[i]=(byte)0;

		return ans;
	}
	private static int[] FIVEDecompress(byte [] arr)
	{
		System.out.println("5");
		if(arr == null || arr.length <= _HEADER_SIZE )
			return null;

		int size = arr.length;
		int header [] = HeaderInterpreter(arr);

		if(header == null || header.length != 2 )
			return null;

		int min = header[_MIN_INDEX];
		int ans_size = header[_SIZE_INDEX];
		int ans [] = new int[ans_size];
		int i =0;

		for (int j=_HEADER_SIZE; i<ans_size&j<size ;) {
			if(i%8==0)
				ans[i++] = ((arr[j] & 0x1F))+min ;
			else if(i%8==1)
				ans[i++] =( ((arr[j++] & 0xE0)>>>5)|(arr[j]&0x3)<<3)+min ;
			else if(i%8==2)
				ans[i++] = ((arr[j] & 0x7C) >>> 2) +min ;
			else if(i%8==3)
				ans[i++] = (((arr[j++] & 0x80) >>> 7)| (arr[j]&0xF)<<1)+min ;
			else if(i%8==4)
				ans[i++] = (((arr[j++] & 0xF0) >>> 4) | ((arr[j]&0x1 ) <<4))+min ;
			else if(i%8==5)
				ans[i++] =((arr[j] & 0x3E) >>> 1)+min ; 
			else if(i%8==6)
				ans[i++] =(((arr[j++]&0xC0)>>>6 ) |((arr[j] & 0x7) << 2) )+min ;
			else 
				ans[i++] = ((arr[j++] & 0xF8)>>>3)+min ;
		}
		while(i<ans_size)
			ans[i++]=(byte)0;
		return ans ;
	}
	private static boolean FIVETest() {
		for (int i = 0; i < _MAX_BITS; i++) {
			for (int j = 3; j <_MAX_ARRAY_SIZE; j++) {
				int [] a = new int [j];
				Arrays.fill(a, i);
				for (int k = 1; k < a.length; k=k+2) {
					a[k]+=31 ;
				}
				if (!Arrays.equals( Decompress(Compress(a)), a)){
					return false ;
				}	
			}
		}
		return true ;
	}

	private static byte[] SIXCompress(int [] arr , int min)
	{
		System.out.print("6");
		if(arr == null)
			return null ;

		int size = arr.length;

		if(size < 2 | _MAX_ARRAY_SIZE <= size)
			return null ;
		final  byte _TYPE = 6 ;
		int ans_size = _HEADER_SIZE +(int)( Math.ceil( (size*_TYPE)/8.0) );
		byte [] ans = new byte[ans_size];
		ans = HeaderInjection(ans, _TYPE, min, size);
		int i = _HEADER_SIZE ;

		for (int j=0,u=0; i < ans_size & j <size;u++) {
			if(u%4==0)
			{
				ans[i] =(byte)(arr[j++]&0x3F) ;
			}
			else if(u%4==1){
				ans[i] =(byte)(ans[i] |((arr[j]&0x3)<< 6));
				ans[++i] =(byte)((arr[j++]&0x3C)>>>2) ;
			}
			else if(u%4==2){
				ans[i] =(byte)(ans[i] | ((arr[j]&0xF)<< 4));
				ans[++i] =(byte)((arr[j++]&0x30)>>>4)  ;
			}
			else {
				ans[i] =(byte)(ans[i] |((arr[j++]&0x3F)<< 2));
				i++;
			}
		}
		while(++i<ans_size)
			ans[i]=(byte)0;

		return ans;
	}
	private static int[] SIXDecompress(byte [] arr)
	{
		System.out.println("6");
		if(arr == null || arr.length <= _HEADER_SIZE )
			return null;

		int size = arr.length;
		int header [] = HeaderInterpreter(arr);

		if(header == null || header.length != 2 )
			return null;

		int min = header[_MIN_INDEX];
		int ans_size = header[_SIZE_INDEX];
		int ans [] = new int[ans_size];
		int i =0;

		for (int j=_HEADER_SIZE; i<ans_size&j<size ;) {
			if(i%4==0)
				ans[i++] =((arr[j] & 0x3F))+min ;
			else if(i%4==1)
				ans[i++] =(((arr[j++] & 0xC0) >>> 6)|((arr[j]&0xF)<<2))+min ;
			else if(i%4==2)
				ans[i++] = (((arr[j++] & 0xF0) >>> 4)|((arr[j] &0x3 )<<4))+min ;
			else 
				ans[i++] = ((arr[j++]&0xFC) >> 2 )+min ;
		}
		while(i<ans_size)
			ans[i++]=(byte)0;
		return ans ;
	}
	private static boolean SIXTest() {
		for (int i = 0; i < _MAX_BITS; i++) {
			for (int j = 3; j <_MAX_ARRAY_SIZE; j++) {
				int [] a = new int [j];
				Arrays.fill(a, i);
				for (int k = 1; k < a.length; k=k+2) {
					a[k]+=63 ;
				}
				if (!Arrays.equals( Decompress(Compress(a)), a)){
					return false ;
				}	
			}
		}
		return true ;
	}

	private static byte[] SEVENCompress(int [] arr , int min)
	{
		System.out.print("7");
		if(arr == null)
			return null ;

		int size = arr.length;

		if(size < 2 | _MAX_ARRAY_SIZE <= size)
			return null ;
		final  byte _TYPE = 7 ;
		int ans_size = _HEADER_SIZE +(int)( Math.ceil( (size*_TYPE)/8.0) );
		byte [] ans = new byte[ans_size];
		ans = HeaderInjection(ans, _TYPE, min, size);
		int i = _HEADER_SIZE ;

		for (int j=0,u=0; i < ans_size & j <size;u++) {
			if(u%8==0)
			{
				ans[i] =(byte)(arr[j++]&0x7F);	
			}
			else if(u%8==1){
				ans[i] =(byte)(ans[i] | ((arr[j]&0x1)<< 7));
				ans[++i] =(byte)((arr[j++]&0x7E)>>>1) ;	 
			}
			else if(u%8==2){
				ans[i] =(byte)(ans[i] | ((arr[j]&0x3)<< 6));
				ans[++i] =(byte) ((arr[j++]&0x7C)>>>2);	
			}
			else if(u%8==3){
				ans[i] =(byte)(ans[i] | ((arr[j]&0x7)<< 5));
				ans[++i] =(byte) ((arr[j++]&0x78)>>>3 );	
			}
			else if(u%8==4){
				ans[i] =(byte)(ans[i] |((arr[j]&0xF)<< 4)) ;
				ans[++i] =(byte) ((arr[j++]&0x70)>>>4);
			}
			else if(u%8==5){
				ans[i] =(byte)(ans[i] |((arr[j]&0x1F)<< 3));
				ans[++i] =(byte) ((arr[j++]&0x60)>>>5);
			}
			else if(u%8==6){
				ans[i] =(byte)(ans[i] |((arr[j]&0x3F)<< 2));
				ans[++i] =(byte) ((arr[j++]&0x40)>>>6);	
			}
			else
			{
				ans[i] =(byte)(ans[i] |((arr[j++]&0x7F)<< 1));
				i++; 
			}
		}
		while(++i<ans_size)
			ans[i]=(byte)0;

		return ans;
	}
	private static int[] SEVENDecompress(byte [] arr)
	{
		System.out.println("7");
		if(arr == null || arr.length <= _HEADER_SIZE )
			return null;

		int size = arr.length;
		int header [] = HeaderInterpreter(arr);

		if(header == null || header.length != 2 )
			return null;

		int min = header[_MIN_INDEX];
		int ans_size = header[_SIZE_INDEX];
		int ans [] = new int[ans_size];
		int i =0;

		for (int j=_HEADER_SIZE; i<ans_size&j<size ;) {
			if(i%8==0){
				ans[i++] =(arr[j] & 0x7F )+min ;
			}
			else if(i%8==1){
				ans[i++] =(((arr[j++] & 0x80) >>> 7)|((arr[j]&0x3F)<<1))+min ;
			}
			else if(i%8==2){
				ans[i++] = (((arr[j++] & 0xC0) >>> 6)|((arr[j] &0x1F )<<2))+min ;
			}
			else if(i%8==3){
				ans[i++] =(((arr[j++] & 0xE0) >>> 5)|((arr[j]&0xF)<<3))+min ;
			}
			else if(i%8==4){
				ans[i++] = (((arr[j++] & 0xF0) >>> 4)|((arr[j] &0x7 )<<4))+min ;
			}
			else if(i%8==5){
				ans[i++] =(((arr[j++] & 0xF8) >>> 3)|((arr[j]&0x3)<<5))+min ;
			}
			else if(i%8==6){
				ans[i++] = (((arr[j++] & 0xFC) >>> 2)|((arr[j] &0x1 )<<6))+min ; 	
			}
			else {
				ans[i++] = ((arr[j++] & 0xFE) >>> 1)+min ;	
			}
		}
		while(i<ans_size)
			ans[i++]=(byte)0;
		return ans ;
	}
	private static boolean SEVENTest() {
		for (int i = 0; i < _MAX_BITS; i++) {
			for (int j = 5; j <_MAX_ARRAY_SIZE; j++) {
				int [] a = new int [j];
				Arrays.fill(a, i);
				for (int k = 1; k < a.length; k=k+2) {
					a[k]+=127 ;
				}
				if (!Arrays.equals( Decompress(Compress(a)), a)){
					return false ;
				}	
			}
		}
		return true ;
	}

	private static byte[] EIGHTCompress(int [] arr , int min)
	{
		System.out.print("8");
		if(arr == null)
			return null ;

		int size = arr.length;

		if(size < 2 | _MAX_ARRAY_SIZE <= size)
			return null ;
		final  byte _TYPE = 8 ;
		int ans_size = _HEADER_SIZE +(int)( Math.ceil( (size*_TYPE)/8.0) );
		byte [] ans = new byte[ans_size];
		ans = HeaderInjection(ans, _TYPE, min, size);
		int i = _HEADER_SIZE ;

		for (int j=0; i < ans_size & j <size;) {
			ans[i++] =(byte)( arr[j++] & 0xFF  ); 
		}
		while(++i<ans_size)
			ans[i]=(byte)0;

		return ans;
	}
	private static int[] EIGHTDecompress(byte [] arr)
	{
		System.out.println("8");
		if(arr == null || arr.length <= _HEADER_SIZE )
			return null;

		int size = arr.length;
		int header [] = HeaderInterpreter(arr);

		if(header == null || header.length != 2 )
			return null;

		int min = header[_MIN_INDEX];
		int ans_size = header[_SIZE_INDEX];
		int ans [] = new int[ans_size];
		int i =0;

		for (int j=_HEADER_SIZE; i<ans_size&j<size ;) {
			ans[i++] = arr[j++] & 0xFF;
		}
		while(i<ans_size)
			ans[i++]=(byte)0;
		return ans ;
	}
	private static boolean EIGHTTest() {
		for (int i = 0; i < _MAX_BITS; i++) {
			for (int j = 5; j <_MAX_ARRAY_SIZE; j++) {
				int [] a = new int [j];
				Arrays.fill(a, i);
				for (int k = 1; k < a.length; k=k+2) {
					a[k]+=255 ;
				}
				if (!Arrays.equals( Decompress(Compress(a)), a)){
					return false ;
				}	
			}
		}
		return true ;
	}

	private static byte[] NINECompress(int [] arr , int min)
	{
		System.out.print("9");
		if(arr == null)
			return null ;

		int size = arr.length;

		if(size < 2 | _MAX_ARRAY_SIZE <= size)
			return null ;

		final  byte _TYPE = 9;

		int ans_size = _HEADER_SIZE +(int)( Math.ceil( (size*_TYPE)/8.0) );
		byte [] ans = new byte[ans_size];
		ans = HeaderInjection(ans, _TYPE, min, size);

		int i = _HEADER_SIZE ;

		for (int j=0,u=0; i < ans_size-1 & j <size;u++) {
			if(u%8==0){
				ans[i] =(byte)(arr[j] & 0xFF);
				ans[++i]=(byte)((arr[j++]&0x100)>>>8);		
			}
			else if(u%8==1){
				ans[i] =(byte)(ans[i] | ((arr[j]&0x7F)<< 1));
				ans[++i]=(byte)((arr[j++]&0x180)>>>7);
			}
			else if(u%8==2){
				ans[i] =(byte)(ans[i] |((arr[j]&0x3F)<< 2) );
				ans[++i]=(byte)((arr[j++]&0x1C0)>>>6);
			}
			else if(u%8==3){
				ans[i] =(byte)(ans[i] | ((arr[j]&0x1F)<< 3));
				ans[++i]=(byte)((arr[j++]&0x1E0)>>>5 );
			}
			else if(u%8==4){
				ans[i] =(byte)(ans[i] |((arr[j]&0xF)<< 4)) ;
				ans[++i]=(byte)((arr[j++]&0x1F0)>>>4 );		
			}
			else if(u%8==5){
				ans[i] =(byte)(ans[i] |((arr[j]&0x7)<<5) );
				ans[++i]=(byte) ((arr[j++]&0x1F8)>>>3 );			
			}
			else if(u%8==6){
				ans[i] =(byte)(ans[i] |((arr[j]&0x3)<< 6));
				ans[++i]=(byte) ((arr[j++]&0x1FC)>>>2 );	 
			}
			else
			{
				ans[i] =(byte)(ans[i] |((arr[j]&0x1)<< 7) );
				ans[++i]=(byte) ((arr[j++]&0x1FE)>>>1);	
				i++;
			}
		}
		while(++i<ans_size)
			ans[i]=(byte)0;

		return ans;
	}
	private static int[] NINEDecompress(byte [] arr)
	{
		System.out.println("9");
		if(arr == null || arr.length <= _HEADER_SIZE )
			return null;

		int size = arr.length;

		int header [] = HeaderInterpreter(arr);

		if(header == null || header.length != 2 )
			return null;

		int min = header[_MIN_INDEX];
		int ans_size = header[_SIZE_INDEX];
		int ans [] = new int[ans_size];
		int i =0;
		for (int j=_HEADER_SIZE; i<ans_size&j<size-1;) {
			if(i%8==0){
				ans[i++] =((arr[j++] & 0xFF) | ((arr[j]&0x1)<<8))+min ;
			}
			else if(i%8==1){
				ans[i++] =(((arr[j++] & 0xFE) >>> 1) | ((arr[j]&0x3)<<7) )+min ;
			}
			else if(i%8==2){
				ans[i++] =(((arr[j++] & 0xFC) >>> 2) | ((arr[j]&0x7)<<6) )+min ;
			}
			else if(i%8==3){
				ans[i++] =(((arr[j++] & 0xF8) >>> 3) | ((arr[j]&0xF)<<5) )+min ;
			}
			else if(i%8==4){
				ans[i++] =(((arr[j++] & 0xF0) >>> 4) | ((arr[j]&0x1F)<<4) )+min ;
			}
			else if(i%8==5){
				ans[i++] =(((arr[j++] & 0xE0) >>> 5) | ((arr[j]&0x3F)<<3) )+min ;
			}
			else if(i%8==6){
				ans[i++] =(((arr[j++] & 0xC0) >>> 6) | ((arr[j]&0x7F)<<2) )+min ;
			}
			else {
				ans[i++] =(((arr[j++] & 0x80) >>> 7) | ((arr[j++]&0xFF)<<1) )+min ;
			}
		}
		while(i<ans_size)
			ans[i++]=(byte)0;
		return ans ;
	}
	private static boolean NINETest() {
		for (int i = 0; i < _MAX_BITS; i++) {
			for (int j = 10; j <_MAX_ARRAY_SIZE; j++) {
				int [] a = new int [j];
				Arrays.fill(a, i);
				for (int k = 1; k < a.length; k=k+2) {
					a[k]+=511 ;
				}
				if (!Arrays.equals( Decompress(Compress(a)), a)){
					return false ;
				}	
			}
		}
		return true ;
	}



	private static byte[] TENCompress(int [] arr , int min)
	{
		System.out.print("10");
		if(arr == null)
			return null ;

		int size = arr.length;

		if(size < 2 | _MAX_ARRAY_SIZE <= size)
			return null ;

		final  byte _TYPE = 10;

		int ans_size = _HEADER_SIZE +(int)( Math.ceil( (size*_TYPE)/8.0) );
		byte [] ans = new byte[ans_size];
		ans = HeaderInjection(ans, _TYPE, min, size);

		int i = _HEADER_SIZE ;

		for (int j=0,u=0; i < ans_size & j <size;u++) {
			if(u%4==0){
				ans[i] =(byte)(arr[j] & 0xFF);
				ans[++i] =(byte) ((arr[j++]&0x300)>>>8);
			}
			else if(u%4==1){
				ans[i] =(byte)(ans[i] |((arr[j]&0x3F)<< 2));
				ans[++i] =(byte)((arr[j++]&0x3C0)>>>6);
			}
			else if(u%4==2){
				ans[i] =(byte)(ans[i] | ((arr[j]&0xF)<< 4) );
				ans[++i] =(byte)((arr[j++]&0x3F0)>>>4);
			}
			else{
				ans[i] =(byte)(ans[i] |((arr[j]&0x3)<< 6));
				ans[++i] =(byte)((arr[j++]&0x3FC)>>>2 );
				i++;
			}
		}
		while(++i<ans_size)
			ans[i]=(byte)0;

		return ans;
	}
	private static int[] TENDecompress(byte [] arr)
	{
		System.out.println("10");
		if(arr == null || arr.length <= _HEADER_SIZE )
			return null;

		int size = arr.length;

		int header [] = HeaderInterpreter(arr);

		if(header == null || header.length != 2 )
			return null;

		int min = header[_MIN_INDEX];
		int ans_size = header[_SIZE_INDEX];
		int ans [] = new int[ans_size];
		int i =0;

		for (int j=_HEADER_SIZE; i<ans_size&j<size-1 ;) {
			if(i%4==0){
				ans[i++] =((arr[j++] & 0xFF) | ((arr[j]&0x3)<<8) )+min ;
			}
			else if(i%4==1){
				ans[i++] =(((arr[j++] & 0xFC) >>> 2) | ((arr[j]&0xF)<<6) )+min ;
			}
			else if(i%4==2){
				ans[i++] =(((arr[j++] & 0xF0) >>> 4) | ((arr[j]&0x3F)<<4) )+min ;
			}
			else{
				ans[i++] =(((arr[j++] & 0xC0) >>> 6) | ((arr[j++]&0xFF)<<2) )+min ;
			}
		}
		while(i<ans_size)
			ans[i++]=(byte)0;
		return ans ;
	}
	private static boolean TENTest() {
		for (int i = 0; i < _MAX_BITS; i++) {
			for (int j = 10; j <_MAX_ARRAY_SIZE; j++) {
				int [] a = new int [j];
				Arrays.fill(a, i);
				for (int k = 1; k < a.length; k=k+2) {
					a[k]+=1023 ;
				}
				if (!Arrays.equals( Decompress(Compress(a)), a)){
					return false ;
				}	
			}
		}
		return true ;
	}

	private static byte[] ELEVENCompress(int [] arr , int min)
	{
		System.out.print("11");
		if(arr == null)
			return null ;

		int size = arr.length;

		if(size < 2 | _MAX_ARRAY_SIZE <= size)
			return null ;

		final  byte _TYPE = 11;

		int ans_size = _HEADER_SIZE +(int)( Math.ceil( (size*_TYPE)/8.0) );
		byte [] ans = new byte[ans_size];
		ans = HeaderInjection(ans, _TYPE, min, size);

		int i = _HEADER_SIZE ;

		for (int j=0,u=0; i < ans_size-1 & j <size;u++) {
			if(u%8==0){
				ans[i] =(byte)(arr[j] & 0xFF);
				ans[++i] =(byte)((arr[j++] & 0x700)>>>8);
			}
			else if(u%8==1){
				ans[i] =(byte)(ans[i] | ((arr[j]&0x1F)<<3));
				ans[++i] =(byte)((arr[j++] & 0x7E0)>>>5);
			}
			else if(u%8==2){
				ans[i] =(byte)(ans[i] |((arr[j]&0x3)<<6));
				ans[++i] =(byte)((arr[j] & 0x3FC)>>>2);
				if(i < ans_size-1 )
					ans[++i] =(byte)((arr[j++] & 0x400)>>>10);
			}
			else if(u%8==3){
				ans[i] =(byte)(ans[i] | ((arr[j]&0x7F)<<1));
				ans[++i] =(byte)((arr[j++] & 0x780)>>>7); 
			}
			else if(u%8==4){
				ans[i] =(byte)(ans[i] | ((arr[j]&0xF)<<4));
				ans[++i] =(byte)((arr[j++] & 0x7F0)>>>4); 	
			}
			else if(u%8==5){
				ans[i] =(byte)(ans[i] |((arr[j]&0x1)<<7));
				ans[++i] =(byte)((arr[j] & 0x1FE)>>>1);
				if(i < ans_size-1 )
					ans[++i] =(byte)((arr[j++] & 0x600)>>>9);	 
			}
			else if(u%8==6){
				ans[i] =(byte)(ans[i] | ((arr[j]&0x3F)<<2));
				ans[++i] =(byte)((arr[j++] & 0x7C0)>>>6); 	
			}
			else
			{
				ans[i] =(byte)(ans[i] | ((arr[j]&0x7)<<5));
				ans[++i] =(byte)((arr[j++] & 0x7F8)>>>3); 	
				i++;
			}
		}
		while(++i<ans_size)
			ans[i]=(byte)0;

		return ans;
	}
	private static int[] ELEVENDecompress(byte [] arr)
	{
		System.out.println("11");
		if(arr == null || arr.length <= _HEADER_SIZE )
			return null;

		int size = arr.length;

		int header [] = HeaderInterpreter(arr);

		if(header == null || header.length != 2 )
			return null;

		int min = header[_MIN_INDEX];
		int ans_size = header[_SIZE_INDEX];
		int ans [] = new int[ans_size];
		int i =0;
		for (int j=_HEADER_SIZE; i<ans_size&j<size ;) {
			if(i%8==0){
				ans[i++] = ((arr[j++]&0xFF)	|	((arr[j]&0x7)<<8)	)+min ;
			}
			else if(i%8==1){
				ans[i++] = (((arr[j++]&0xF8)>>3)	|	((arr[j]&0x3F)<<5)	)+min ;
			}
			else if(i%8==2){
				ans[i++] = (((arr[j++]&0xC0)>>6)	|	((arr[j++]&0xFF)<<2)	| ((arr[j]&0x1)<<10)	);
			}
			else if(i%8==3){
				ans[i++] = (((arr[j++]&0xFE)>>1)	|	((arr[j]&0xF)<<7)	)+min ;
			}
			else if(i%8==4){
				ans[i++] = (((arr[j++]&0xF0)>>4)	|	((arr[j]&0x7F)<<4)	)+min ;
			}
			else if(i%8==5){
				ans[i++] = (((arr[j++]&0x80)>>7)	|	((arr[j++]&0xFF)<<1)	|	((arr[j]&0x3)<<9)	)+min ;
			}
			else if(i%8==6){
				ans[i++] = (((arr[j++]&0xFC)>>2)	|	((arr[j]&0x1F)<<6))+min ;
			}
			else {
				ans[i++] = (((arr[j++]&0xE0)>>5)	|	((arr[j++]&0xFF)<<3))+min ;
			}
		}
		while(i<ans_size)
			ans[i++]=(byte)0;
		return ans ;
	}
	private static boolean ELEVENTest() {
		for (int i = 0; i < _MAX_BITS; i++) {
			for (int j = 10; j <_MAX_ARRAY_SIZE; j++) {
				int [] a = new int [j];
				Arrays.fill(a, i);
				for (int k = 1; k < a.length; k=k+2) {
					a[k]+=2047 ;
				}
				if (!Arrays.equals( Decompress(Compress(a)), a)){
					return false ;
				}	
			}
		}
		return true ;
	}

	private static byte[] TWELEFCompress(int [] arr , int min)
	{
		System.out.print("12");
		if(arr == null)
			return null ;

		int size = arr.length;

		if(size < 2 | _MAX_ARRAY_SIZE <= size)
			return null ;

		final  byte _TYPE = 12;

		int ans_size = _HEADER_SIZE +(int)( Math.ceil( (size*_TYPE)/8.0) );
		byte [] ans = new byte[ans_size];
		ans = HeaderInjection(ans, _TYPE, min, size);

		int i = _HEADER_SIZE ;

		for (int j=0,u=0; i < ans_size & j <size;u++) {
			if(u%2==0){
				ans[i++] = (byte) (arr[j] & 0xFF);
				ans[i] = (byte) ((arr[j++] & 0xF00)>>>8);
			}
			else {
				ans[i] =(byte)(ans[i] |((arr[j]&0xF)<<4));
				ans[++i] = (byte) ((arr[j++] & 0xFF0)>>>4);
				i++;
			}
		}
		while(++i<ans_size)
			ans[i]=(byte)0;

		return ans;
	}
	private static int[] TWELEFDecompress(byte [] arr)
	{
		System.out.println("12");
		if(arr == null || arr.length <= _HEADER_SIZE )
			return null;

		int size = arr.length;

		int header [] = HeaderInterpreter(arr);

		if(header == null || header.length != 2 )
			return null;

		int min = header[_MIN_INDEX];
		int ans_size = header[_SIZE_INDEX];
		int ans [] = new int[ans_size];
		int i =0;
		for (int j=_HEADER_SIZE; i<ans_size&j<size ;) {
			if(i%2==0){
				ans[i++] = (	(arr[j++]&0xFF)	|	((arr[j]&0xF)<<8)	)+min ;
			}
			else {
				ans[i++] = (	((arr[j++]&0xF0)>>4)	|	((arr[j++]&0xFF)<<4)	)+min ;
			}
		}
		while(i<ans_size)
			ans[i++]=(byte)0;
		return ans ;
	}
	private static boolean TWELEFTest() {
		for (int i = 0; i < _MAX_BITS; i++) {
			for (int j = 10; j <_MAX_ARRAY_SIZE; j++) {
				int [] a = new int [j];
				Arrays.fill(a, i);
				for (int k = 1; k < a.length; k=k+2) {
					a[k]+=4095 ;
				}
				if (!Arrays.equals( Decompress(Compress(a)), a)){
					return false ;
				}	
			}
		}
		return true ;
	}

	private static byte[] THIRTEENCompress(int [] arr , int min)
	{
		System.out.print("13");
		if(arr == null)
			return null ;

		int size = arr.length;

		if(size < 2 | _MAX_ARRAY_SIZE <= size)
			return null ;

		final  byte _TYPE = 13;

		int ans_size = _HEADER_SIZE +(int)( Math.ceil( (size*_TYPE)/8.0) );
		byte [] ans = new byte[ans_size];
		ans = HeaderInjection(ans, _TYPE, min, size);

		int i = _HEADER_SIZE ;
		for (int j=0,u=0; i < ans_size & j <size;u++) {
			if(u%8==0){
				ans[i] =(byte)(arr[j] & 0xFF);
				ans[++i] =(byte)((arr[j++]&0x1F00)>>>8);
			}
			else if(u%8==1){
				ans[i] =(byte)(ans[i] | ((arr[j]&0x7)<<5));
				ans[++i] =(byte) ((arr[j]&0x7F8)>>>3);
				ans[++i] =(byte) ((arr[j++]&0x1800)>>>11);
			}
			else if(u%8==2){
				ans[i] =(byte)(ans[i] | ((arr[j]&0x3F)<<2) );
				ans[++i] =(byte) ((arr[j++]&0x1FC0)>>>6); 
			}
			else if(u%8==3){
				ans[i] =(byte)(ans[i] | ((arr[j]&0x1)<<7));
				ans[++i] =(byte) ((arr[j]&0x1FE)>>>1);
				ans[++i] =(byte) ((arr[j++]&0x1E00)>>>9);		
			}
			else if(u%8==4){
				ans[i] =(byte)(ans[i] | ((arr[j]&0xF)<<4) );
				ans[++i] =(byte) ((arr[j]&0xFF0)>>>4);
				ans[++i] =(byte) ((arr[j++]&0x1000)>>>12) ;	
			}
			else if(u%8==5){
				ans[i] =(byte)(ans[i] | ((arr[j]&0x7F)<<1));
				ans[++i] =(byte) ((arr[j++]&0x1F80)>>>7); 		 
			}
			else if(u%8==6){
				ans[i] =(byte)(ans[i] |((arr[j]&0x3)<<6));
				ans[++i] =(byte) ((arr[j]&0x3FC)>>>2);
				ans[++i] =(byte) ((arr[j++]&0x1C00)>>>10) ;	

			}
			else
			{
				ans[i] =(byte)(ans[i] |  ((arr[j]&0x1F)<<3));
				ans[++i] =(byte)  (	(arr[j++]&0x1FE0)>>>5	); 	
				i++;
			}
		}
		while(++i<ans_size)
			ans[i]=(byte)0;

		return ans;
	}
	private static int[] THIRTEENDecompress(byte [] arr)
	{
		System.out.println("13");
		if(arr == null || arr.length <= _HEADER_SIZE )
			return null;

		int size = arr.length;

		int header [] = HeaderInterpreter(arr);

		if(header == null || header.length != 2 )
			return null;

		int min = header[_MIN_INDEX];
		int ans_size = header[_SIZE_INDEX];
		int ans [] = new int[ans_size];
		int i =0;
		for (int j=_HEADER_SIZE; i<ans_size&j<size ;) {
			if(i%8==0){
				ans[i++] =  (	(arr[j++]&0xFF) | ((arr[j]&0x1F)<<8)	)+min ;
			}
			else if(i%8==1){
				ans[i++] =  (	((arr[j++]&0xE0)>>>5)	|	((arr[j++]&0xFF)<<3)	|	((arr[j]&0x3)<<11))+min ;
			}
			else if(i%8==2){
				ans[i++] = (	((arr[j++]&0xFC)>>>2) |	((arr[j]&0x7F)<<6))	+min ;	
			}
			else if(i%8==3){
				ans[i++] =  (	((arr[j++]&0x80)>>>7)	| ((arr[j++]&0xFF)<<1)  |  ((arr[j]&0xF)<<9)  )+min ;
			}
			else if(i%8==4){
				ans[i++] = (	((arr[j++]&0xF0)>>>4)|((arr[j++]&0xFF)<<4) | ((arr[j]&0x1)<<12)	)+min ;	
			}
			else if(i%8==5){
				ans[i++] = (	((arr[j++]&0xFE)>>>1)|((arr[j]&0x3F)<<7))+min ;
			}
			else if(i%8==6){
				ans[i++] = (	((arr[j++]&0xC0)>>>6 ) | ((arr[j++]&0xFF)<<2) | ((arr[j]&0x7)<<10)	)+min ;
			}
			else {
				ans[i++] = (	((arr[j++])&0xF8)>>>3 | ((arr[j++]&0xFF)<<5)	)+min ;
			}
		}
		while(i<ans_size)
			ans[i++]=(byte)0;
		return ans ;
	}
	private static boolean THIRTEENTest() {
		for (int i = 0; i < _MAX_BITS; i++) {
			for (int j = 10; j <_MAX_ARRAY_SIZE; j++) {
				int [] a = new int [j];
				Arrays.fill(a, i);
				for (int k = 1; k < a.length; k=k+2) {
					a[k]+=8191 ;
				}
				if (!Arrays.equals( Decompress(Compress(a)), a)){
					return false ;
				}	
			}
		}
		return true ;
	}

	private static byte[] FOURTEENCompress(int [] arr , int min)
	{
		System.out.print("14");
		if(arr == null)
			return null ;

		int size = arr.length;

		if(size < 2 | _MAX_ARRAY_SIZE <= size)
			return null ;

		final  byte _TYPE = 14;

		int ans_size = _HEADER_SIZE +(int)( Math.ceil( (size*_TYPE)/8.0) );
		byte [] ans = new byte[ans_size];
		ans = HeaderInjection(ans, _TYPE, min, size);

		int i = _HEADER_SIZE ;

		for (int j=0,u=0; i < ans_size & j <size;u++) {
			if(u%4==0){
				ans[i] =(byte)(arr[j] & 0xFF);
				ans[++i] =(byte)((arr[j++]&0x3F00)>>8);
			}
			else if(u%4==1){
				ans[i] =(byte)(ans[i] |((arr[j]&0x3)<<6));
				ans[++i] =(byte)((arr[j]&0x3FC)>>>2);
				ans[++i] =(byte)((arr[j++]&0x3C00)>>>10);	
			}
			else if(u%4==2){
				ans[i] =(byte)(ans[i] |((arr[j]&0xF)<<4));
				ans[++i] =(byte)((arr[j]&0xFF0)>>4);
				ans[++i] =(byte) ((arr[j++]&0x3000)>>>12);			
			}
			else {
				ans[i] =(byte)(ans[i] |((arr[j]&0x3F)<<2));
				ans[++i] =(byte)((arr[j++]&0x3FC0)>>>6);
				i++;
			}
		}
		while(++i<ans_size)
			ans[i]=(byte)0;

		return ans;
	}
	private static int[] FOURTEENDecompress(byte [] arr)
	{
		System.out.println("14");
		if(arr == null || arr.length <= _HEADER_SIZE )
			return null;

		int size = arr.length;

		int header [] = HeaderInterpreter(arr);

		if(header == null || header.length != 2 )
			return null;

		int min = header[_MIN_INDEX];
		int ans_size = header[_SIZE_INDEX];
		int ans [] = new int[ans_size];
		int i =0;
		for (int j=_HEADER_SIZE; i<ans_size&j<size ;) {
			if(i%4==0){
				ans[i++] = ((arr[j++]&0xFF)|((arr[j]&0x3F)<<8))+min ;
			}
			else if(i%4==1){
				ans[i++] = ( ((arr[j++]&0xC0)>>>6)|((arr[j++]&0xFF)<<2)|((arr[j]&0xF)<<10))+min ;
			}
			else if(i%4==2){
				ans[i++] = (((arr[j++]&0xF0)>>>4)|((arr[j++]&0xFF)<<4)|((arr[j]&0x3)<<12)	)+min ;
			}
			else {
				ans[i++] = ( ((arr[j++]&0xFC)>>2)|((arr[j++]&0xFF)<<6)	)+min ;
			}
		}
		while(i<ans_size)
			ans[i++]=(byte)0;
		return ans ;
	}
	private static boolean FOURTEENTest() {
		for (int i = 0; i < _MAX_BITS; i++) {
			for (int j = 10; j <_MAX_ARRAY_SIZE; j++) {
				int [] a = new int [j];
				Arrays.fill(a, i);
				for (int k = 1; k < a.length; k=k+2) {
					a[k]+=16382 ;
				}
				if (!Arrays.equals( Decompress(Compress(a)), a)){
					return false ;
				}	
			}
		}
		return true ;
	}


	public static void main(String [] args)
	{
		int [] a = new int [100000];
		for(int i=0 ;i<a.length;i++)
			a[i] = i %8500;
		System.out.println(Arrays.equals(Decompress(Compress(a)), a));
		
		
		
	}

}
