<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>云造库存管理</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<style>
      /* NOTE: The styles were added inline because Prefixfree needs access to your styles and they must be inlined if they are on local disk! */
     
.btn { display: inline-block; *display: inline; *zoom: 1; padding: 4px 10px 4px; margin-bottom: 0; font-size: 13px; line-height: 18px; color: #333333; text-align: center;text-shadow: 0 1px 1px rgba(255, 255, 255, 0.75); vertical-align: middle; background-color: #f5f5f5; background-image: -moz-linear-gradient(top, #ffffff, #e6e6e6); background-image: -ms-linear-gradient(top, #ffffff, #e6e6e6); background-image: -webkit-gradient(linear, 0 0, 0 100%, from(#ffffff), to(#e6e6e6)); background-image: -webkit-linear-gradient(top, #ffffff, #e6e6e6); background-image: -o-linear-gradient(top, #ffffff, #e6e6e6); background-image: linear-gradient(top, #ffffff, #e6e6e6); background-repeat: repeat-x; filter: progid:dximagetransform.microsoft.gradient(startColorstr=#ffffff, endColorstr=#e6e6e6, GradientType=0); border-color: #e6e6e6 #e6e6e6 #e6e6e6; border-color: rgba(0, 0, 0, 0.1) rgba(0, 0, 0, 0.1) rgba(0, 0, 0, 0.25); border: 1px solid #e6e6e6; -webkit-border-radius: 4px; -moz-border-radius: 4px; border-radius: 4px; -webkit-box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.2), 0 1px 2px rgba(0, 0, 0, 0.05); -moz-box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.2), 0 1px 2px rgba(0, 0, 0, 0.05); box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.2), 0 1px 2px rgba(0, 0, 0, 0.05); cursor: pointer; *margin-left: .3em; }
.btn:hover, .btn:active, .btn.active, .btn.disabled, .btn[disabled] { background-color: #e6e6e6; }
.btn-large { padding: 9px 14px; font-size: 15px; line-height: normal; -webkit-border-radius: 5px; -moz-border-radius: 5px; border-radius: 5px; }
.btn:hover { color: #333333; text-decoration: none; background-color: #e6e6e6; background-position: 0 -15px; -webkit-transition: background-position 0.1s linear; -moz-transition: background-position 0.1s linear; -ms-transition: background-position 0.1s linear; -o-transition: background-position 0.1s linear; transition: background-position 0.1s linear; }
.btn-primary, .btn-primary:hover { text-shadow: 0 -1px 0 rgba(0, 0, 0, 0.25); color: #ffffff; }
.btn-primary.active { color: rgba(255, 255, 255, 0.75); }
.btn-primary { background-color: #4a77d4; background-image: -moz-linear-gradient(top, #6eb6de, #4a77d4); background-image: -ms-linear-gradient(top, #6eb6de, #4a77d4); background-image: -webkit-gradient(linear, 0 0, 0 100%, from(#6eb6de), to(#4a77d4)); background-image: -webkit-linear-gradient(top, #6eb6de, #4a77d4); background-image: -o-linear-gradient(top, #6eb6de, #4a77d4); background-image: linear-gradient(top, #6eb6de, #4a77d4); background-repeat: repeat-x; filter: progid:dximagetransform.microsoft.gradient(startColorstr=#6eb6de, endColorstr=#4a77d4, GradientType=0);  border: 1px solid #3762bc; text-shadow: 1px 1px 1px rgba(0,0,0,0.4); box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.2), 0 1px 2px rgba(0, 0, 0, 0.5); }
.btn-primary:hover, .btn-primary:active, .btn-primary.active, .btn-primary.disabled, .btn-primary[disabled] { filter: none; background-color: #4a77d4; }
.btn-block { width: 100%; display:block; }

* { -webkit-box-sizing:border-box; -moz-box-sizing:border-box; -ms-box-sizing:border-box; -o-box-sizing:border-box; box-sizing:border-box; }

html { width: 100%; height:100%; overflow:hidden; }

body { 
	width: 100%;
	height:100%;
	font-family: 'Open Sans', sans-serif;
	background: #092756;
	background: -moz-radial-gradient(0% 100%, ellipse cover, rgba(104,128,138,.4) 10%,rgba(138,114,76,0) 40%),-moz-linear-gradient(top,  rgba(57,173,219,.25) 0%, rgba(42,60,87,.4) 100%), -moz-linear-gradient(-45deg,  #670d10 0%, #092756 100%);
	background: -webkit-radial-gradient(0% 100%, ellipse cover, rgba(104,128,138,.4) 10%,rgba(138,114,76,0) 40%), -webkit-linear-gradient(top,  rgba(57,173,219,.25) 0%,rgba(42,60,87,.4) 100%), -webkit-linear-gradient(-45deg,  #670d10 0%,#092756 100%);
	background: -o-radial-gradient(0% 100%, ellipse cover, rgba(104,128,138,.4) 10%,rgba(138,114,76,0) 40%), -o-linear-gradient(top,  rgba(57,173,219,.25) 0%,rgba(42,60,87,.4) 100%), -o-linear-gradient(-45deg,  #670d10 0%,#092756 100%);
	background: -ms-radial-gradient(0% 100%, ellipse cover, rgba(104,128,138,.4) 10%,rgba(138,114,76,0) 40%), -ms-linear-gradient(top,  rgba(57,173,219,.25) 0%,rgba(42,60,87,.4) 100%), -ms-linear-gradient(-45deg,  #670d10 0%,#092756 100%);
	background: -webkit-radial-gradient(0% 100%, ellipse cover, rgba(104,128,138,.4) 10%,rgba(138,114,76,0) 40%), linear-gradient(to bottom,  rgba(57,173,219,.25) 0%,rgba(42,60,87,.4) 100%), linear-gradient(135deg,  #670d10 0%,#092756 100%);
	filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#3E1D6D', endColorstr='#092756',GradientType=1 );
}
.login { 
	position: absolute;
	top: 35%;
	left: 50%;
	margin: -150px 0 0 -150px;
	width:350px;
	height:300px;
}
.login h1,h2,h3 { color: #fff; text-shadow: 0 0 10px rgba(0,0,0,0.3); letter-spacing:1px; text-align:center; }

input { 
	width: 100%; 
	margin-bottom: 10px; 
	background: rgba(0,0,0,0.3);
	border: none;
	outline: none;
	padding: 10px;
	font-size: 13px;
	color: #fff;
	text-shadow: 1px 1px 1px rgba(0,0,0,0.3);
	border: 1px solid rgba(0,0,0,0.3);
	border-radius: 4px;
	box-shadow: inset 0 -5px 45px rgba(100,100,100,0.2), 0 1px 1px rgba(255,255,255,0.2);
	-webkit-transition: box-shadow .5s ease;
	-moz-transition: box-shadow .5s ease;
	-o-transition: box-shadow .5s ease;
	-ms-transition: box-shadow .5s ease;
	transition: box-shadow .5s ease;
}
input:focus { box-shadow: inset 0 -5px 45px rgba(100,100,100,0.6), 0 1px 1px rgba(255,255,255,0.7); }

    </style>   
</head>
<body >

<div class="login">
	<h1>云造库存系统V1.0</h1>
	<h2>用户登录</h2>
    <form action="loginWeb" method="post" onsubmit="encodePass();">
    	<input type="text" name="username" placeholder="用户名" required="required" />
        <input id="login_userpass" type="password" placeholder="密码" required="required" />
        
        <button type="submit" class="btn btn-primary btn-block btn-large">登录</button><input id="login_userpass2" type="hidden" name="password" required placeholder="密码" type="password" value='' />
    </form>
    <h3>${obj}</h3>   
</div>
<script src="js/jquery.min.js"></script>
<script type="text/javascript">

	function encodePass(){
		var _password=encodeMD5($('#login_userpass').val());
		$('#login_userpass2').val(_password);
		$('#login_userpass').val('');
	}
	
	/**
	 * 明文MD5加密
	 * @param data
	 * @returns
	 */
	function encodeMD5(data) {

		   // convert number to (unsigned) 32 bit hex, zero filled string
		   function to_zerofilled_hex(n) {     
		       var t1 = (n >>> 0).toString(16)
		       return "00000000".substr(0, 8 - t1.length) + t1
		   }

		   // convert array of chars to array of bytes 
		   function chars_to_bytes(ac) {
		       var retval = []
		       for (var i = 0; i < ac.length; i++) {
		           retval = retval.concat(str_to_bytes(ac[i]))
		       }
		       return retval
		   }


		   // convert a 64 bit unsigned number to array of bytes. Little endian
		   function int64_to_bytes(num) {
		       var retval = []
		       for (var i = 0; i < 8; i++) {
		           retval.push(num & 0xFF)
		           num = num >>> 8
		       }
		       return retval
		   }

		   //  32 bit left-rotation
		   function rol(num, places) {
		       return ((num << places) & 0xFFFFFFFF) | (num >>> (32 - places))
		   }

		   // The 4 MD5 functions
		   function fF(b, c, d) {
		       return (b & c) | (~b & d)
		   }

		   function fG(b, c, d) {
		       return (d & b) | (~d & c)
		   }

		   function fH(b, c, d) {
		       return b ^ c ^ d
		   }

		   function fI(b, c, d) {
		       return c ^ (b | ~d)
		   }

		   // pick 4 bytes at specified offset. Little-endian is assumed
		   function bytes_to_int32(arr, off) {
		       return (arr[off + 3] << 24) | (arr[off + 2] << 16) | (arr[off + 1] << 8) | (arr[off])
		   }

		   /*
		   Conver string to array of bytes in UTF-8 encoding
		   See: 
		   http://www.dangrossman.info/2007/05/25/handling-utf-8-in-javascript-php-and-non-utf8-databases/
		   http://stackoverflow.com/questions/1240408/reading-bytes-from-a-javascript-string
		   How about a String.getBytes(<ENCODING>) for Javascript!? Isn't it time to add it?
		   */
		   function str_to_bytes(str) {
		       var retval = [ ]
		       for (var i = 0; i < str.length; i++)
		           if (str.charCodeAt(i) <= 0x7F) {
		               retval.push(str.charCodeAt(i))
		           } else {
		               var tmp = encodeURIComponent(str.charAt(i)).substr(1).split('%')
		               for (var j = 0; j < tmp.length; j++) {
		                   retval.push(parseInt(tmp[j], 0x10))
		               }
		           }
		       return retval
		   }


		   // convert the 4 32-bit buffers to a 128 bit hex string. (Little-endian is assumed)
		   function int128le_to_hex(a, b, c, d) {
		       var ra = ""
		       var t = 0
		       var ta = 0
		       for (var i = 3; i >= 0; i--) {
		           ta = arguments[i]
		           t = (ta & 0xFF)
		           ta = ta >>> 8
		           t = t << 8
		           t = t | (ta & 0xFF)
		           ta = ta >>> 8
		           t = t << 8
		           t = t | (ta & 0xFF)
		           ta = ta >>> 8
		           t = t << 8
		           t = t | ta
		           ra = ra + to_zerofilled_hex(t)
		       }
		       return ra
		   }

		   // conversion from typed byte array to plain javascript array 
		   function typed_to_plain(tarr) {
		       var retval = new Array(tarr.length)
		       for (var i = 0; i < tarr.length; i++) {
		           retval[i] = tarr[i]
		       }
		       return retval
		   }

		   // check input data type and perform conversions if needed
		   var databytes = null
		   // String
		   var type_mismatch = null
		   if (typeof data == 'string') {
		       // convert string to array bytes
		       databytes = str_to_bytes(data)
		   } else if (data.constructor == Array) {
		       if (data.length === 0) {
		           // if it's empty, just assume array of bytes
		           databytes = data
		       } else if (typeof data[0] == 'string') {
		           databytes = chars_to_bytes(data)
		       } else if (typeof data[0] == 'number') {
		           databytes = data
		       } else {
		           type_mismatch = typeof data[0]
		       }
		   } else if (typeof ArrayBuffer != 'undefined') {
		       if (data instanceof ArrayBuffer) {
		           databytes = typed_to_plain(new Uint8Array(data))
		       } else if ((data instanceof Uint8Array) || (data instanceof Int8Array)) {
		           databytes = typed_to_plain(data)
		       } else if ((data instanceof Uint32Array) || (data instanceof Int32Array) || 
		              (data instanceof Uint16Array) || (data instanceof Int16Array) || 
		              (data instanceof Float32Array) || (data instanceof Float64Array)
		        ) {
		           databytes = typed_to_plain(new Uint8Array(data.buffer))
		       } else {
		           type_mismatch = typeof data
		       }   
		   } else {
		       type_mismatch = typeof data
		   }

		   if (type_mismatch) {
		       alert('MD5 type mismatch, cannot process ' + type_mismatch)
		   }

		   function _add(n1, n2) {
		       return 0x0FFFFFFFF & (n1 + n2)
		   }


		   return do_digest()

		   function do_digest() {

		       // function update partial state for each run
		       function updateRun(nf, sin32, dw32, b32) {
		           var temp = d
		           d = c
		           c = b
		           //b = b + rol(a + (nf + (sin32 + dw32)), b32)
		           b = _add(b, 
		               rol( 
		                   _add(a, 
		                       _add(nf, _add(sin32, dw32))
		                   ), b32
		               )
		           )
		           a = temp
		       }

		       // save original length
		       var org_len = databytes.length

		       // first append the "1" + 7x "0"
		       databytes.push(0x80)

		       // determine required amount of padding
		       var tail = databytes.length % 64
		       // no room for msg length?
		       if (tail > 56) {
		           // pad to next 512 bit block
		           for (var i = 0; i < (64 - tail); i++) {
		               databytes.push(0x0)
		           }
		           tail = databytes.length % 64
		       }
		       for (i = 0; i < (56 - tail); i++) {
		           databytes.push(0x0)
		       }
		       // message length in bits mod 512 should now be 448
		       // append 64 bit, little-endian original msg length (in *bits*!)
		       databytes = databytes.concat(int64_to_bytes(org_len * 8))

		       // initialize 4x32 bit state
		       var h0 = 0x67452301
		       var h1 = 0xEFCDAB89
		       var h2 = 0x98BADCFE
		       var h3 = 0x10325476

		       // temp buffers
		       var a = 0, b = 0, c = 0, d = 0

		       // Digest message
		       for (i = 0; i < databytes.length / 64; i++) {
		           // initialize run
		           a = h0
		           b = h1
		           c = h2
		           d = h3

		           var ptr = i * 64

		           // do 64 runs
		           updateRun(fF(b, c, d), 0xd76aa478, bytes_to_int32(databytes, ptr), 7)
		           updateRun(fF(b, c, d), 0xe8c7b756, bytes_to_int32(databytes, ptr + 4), 12)
		           updateRun(fF(b, c, d), 0x242070db, bytes_to_int32(databytes, ptr + 8), 17)
		           updateRun(fF(b, c, d), 0xc1bdceee, bytes_to_int32(databytes, ptr + 12), 22)
		           updateRun(fF(b, c, d), 0xf57c0faf, bytes_to_int32(databytes, ptr + 16), 7)
		           updateRun(fF(b, c, d), 0x4787c62a, bytes_to_int32(databytes, ptr + 20), 12)
		           updateRun(fF(b, c, d), 0xa8304613, bytes_to_int32(databytes, ptr + 24), 17)
		           updateRun(fF(b, c, d), 0xfd469501, bytes_to_int32(databytes, ptr + 28), 22)
		           updateRun(fF(b, c, d), 0x698098d8, bytes_to_int32(databytes, ptr + 32), 7)
		           updateRun(fF(b, c, d), 0x8b44f7af, bytes_to_int32(databytes, ptr + 36), 12)
		           updateRun(fF(b, c, d), 0xffff5bb1, bytes_to_int32(databytes, ptr + 40), 17)
		           updateRun(fF(b, c, d), 0x895cd7be, bytes_to_int32(databytes, ptr + 44), 22)
		           updateRun(fF(b, c, d), 0x6b901122, bytes_to_int32(databytes, ptr + 48), 7)
		           updateRun(fF(b, c, d), 0xfd987193, bytes_to_int32(databytes, ptr + 52), 12)
		           updateRun(fF(b, c, d), 0xa679438e, bytes_to_int32(databytes, ptr + 56), 17)
		           updateRun(fF(b, c, d), 0x49b40821, bytes_to_int32(databytes, ptr + 60), 22)
		           updateRun(fG(b, c, d), 0xf61e2562, bytes_to_int32(databytes, ptr + 4), 5)
		           updateRun(fG(b, c, d), 0xc040b340, bytes_to_int32(databytes, ptr + 24), 9)
		           updateRun(fG(b, c, d), 0x265e5a51, bytes_to_int32(databytes, ptr + 44), 14)
		           updateRun(fG(b, c, d), 0xe9b6c7aa, bytes_to_int32(databytes, ptr), 20)
		           updateRun(fG(b, c, d), 0xd62f105d, bytes_to_int32(databytes, ptr + 20), 5)
		           updateRun(fG(b, c, d), 0x2441453, bytes_to_int32(databytes, ptr + 40), 9)
		           updateRun(fG(b, c, d), 0xd8a1e681, bytes_to_int32(databytes, ptr + 60), 14)
		           updateRun(fG(b, c, d), 0xe7d3fbc8, bytes_to_int32(databytes, ptr + 16), 20)
		           updateRun(fG(b, c, d), 0x21e1cde6, bytes_to_int32(databytes, ptr + 36), 5)
		           updateRun(fG(b, c, d), 0xc33707d6, bytes_to_int32(databytes, ptr + 56), 9)
		           updateRun(fG(b, c, d), 0xf4d50d87, bytes_to_int32(databytes, ptr + 12), 14)
		           updateRun(fG(b, c, d), 0x455a14ed, bytes_to_int32(databytes, ptr + 32), 20)
		           updateRun(fG(b, c, d), 0xa9e3e905, bytes_to_int32(databytes, ptr + 52), 5)
		           updateRun(fG(b, c, d), 0xfcefa3f8, bytes_to_int32(databytes, ptr + 8), 9)
		           updateRun(fG(b, c, d), 0x676f02d9, bytes_to_int32(databytes, ptr + 28), 14)
		           updateRun(fG(b, c, d), 0x8d2a4c8a, bytes_to_int32(databytes, ptr + 48), 20)
		           updateRun(fH(b, c, d), 0xfffa3942, bytes_to_int32(databytes, ptr + 20), 4)
		           updateRun(fH(b, c, d), 0x8771f681, bytes_to_int32(databytes, ptr + 32), 11)
		           updateRun(fH(b, c, d), 0x6d9d6122, bytes_to_int32(databytes, ptr + 44), 16)
		           updateRun(fH(b, c, d), 0xfde5380c, bytes_to_int32(databytes, ptr + 56), 23)
		           updateRun(fH(b, c, d), 0xa4beea44, bytes_to_int32(databytes, ptr + 4), 4)
		           updateRun(fH(b, c, d), 0x4bdecfa9, bytes_to_int32(databytes, ptr + 16), 11)
		           updateRun(fH(b, c, d), 0xf6bb4b60, bytes_to_int32(databytes, ptr + 28), 16)
		           updateRun(fH(b, c, d), 0xbebfbc70, bytes_to_int32(databytes, ptr + 40), 23)
		           updateRun(fH(b, c, d), 0x289b7ec6, bytes_to_int32(databytes, ptr + 52), 4)
		           updateRun(fH(b, c, d), 0xeaa127fa, bytes_to_int32(databytes, ptr), 11)
		           updateRun(fH(b, c, d), 0xd4ef3085, bytes_to_int32(databytes, ptr + 12), 16)
		           updateRun(fH(b, c, d), 0x4881d05, bytes_to_int32(databytes, ptr + 24), 23)
		           updateRun(fH(b, c, d), 0xd9d4d039, bytes_to_int32(databytes, ptr + 36), 4)
		           updateRun(fH(b, c, d), 0xe6db99e5, bytes_to_int32(databytes, ptr + 48), 11)
		           updateRun(fH(b, c, d), 0x1fa27cf8, bytes_to_int32(databytes, ptr + 60), 16)
		           updateRun(fH(b, c, d), 0xc4ac5665, bytes_to_int32(databytes, ptr + 8), 23)
		           updateRun(fI(b, c, d), 0xf4292244, bytes_to_int32(databytes, ptr), 6)
		           updateRun(fI(b, c, d), 0x432aff97, bytes_to_int32(databytes, ptr + 28), 10)
		           updateRun(fI(b, c, d), 0xab9423a7, bytes_to_int32(databytes, ptr + 56), 15)
		           updateRun(fI(b, c, d), 0xfc93a039, bytes_to_int32(databytes, ptr + 20), 21)
		           updateRun(fI(b, c, d), 0x655b59c3, bytes_to_int32(databytes, ptr + 48), 6)
		           updateRun(fI(b, c, d), 0x8f0ccc92, bytes_to_int32(databytes, ptr + 12), 10)
		           updateRun(fI(b, c, d), 0xffeff47d, bytes_to_int32(databytes, ptr + 40), 15)
		           updateRun(fI(b, c, d), 0x85845dd1, bytes_to_int32(databytes, ptr + 4), 21)
		           updateRun(fI(b, c, d), 0x6fa87e4f, bytes_to_int32(databytes, ptr + 32), 6)
		           updateRun(fI(b, c, d), 0xfe2ce6e0, bytes_to_int32(databytes, ptr + 60), 10)
		           updateRun(fI(b, c, d), 0xa3014314, bytes_to_int32(databytes, ptr + 24), 15)
		           updateRun(fI(b, c, d), 0x4e0811a1, bytes_to_int32(databytes, ptr + 52), 21)
		           updateRun(fI(b, c, d), 0xf7537e82, bytes_to_int32(databytes, ptr + 16), 6)
		           updateRun(fI(b, c, d), 0xbd3af235, bytes_to_int32(databytes, ptr + 44), 10)
		           updateRun(fI(b, c, d), 0x2ad7d2bb, bytes_to_int32(databytes, ptr + 8), 15)
		           updateRun(fI(b, c, d), 0xeb86d391, bytes_to_int32(databytes, ptr + 36), 21)

		           // update buffers
		           h0 = _add(h0, a)
		           h1 = _add(h1, b)
		           h2 = _add(h2, c)
		           h3 = _add(h3, d)
		       }
		       // Done! Convert buffers to 128 bit (LE)
		       return int128le_to_hex(h3, h2, h1, h0).toUpperCase();
		   }   
	}
		
</script>
</html>
