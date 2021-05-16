/*
 * Copyright (C) 2015-2020 figurefix <figurefix@outlook.com>
 * 
 * This file is part of prac, 
 * prac is free software released under the MIT license.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining 
 * a copy of this software and associated documentation files (the 
 * "Software"), to deal in the Software without restriction, including 
 * without limitation the rights to use, copy, modify, merge, publish, 
 * distribute, sublicense, and/or sell copies of the Software, and to 
 * permit persons to whom the Software is furnished to do so, subject to 
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be 
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE 
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION 
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */

package figurefix.prac.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class Utilx {
	
	public static void threadSleep(long time) {
		try {
			Thread.sleep(time);
		} catch (Exception e) {

		}
	}
	
	public static boolean isLocalAddress(String ip) throws SocketException {
		Enumeration<NetworkInterface> netinf = NetworkInterface.getNetworkInterfaces();
		while(netinf.hasMoreElements()) {
			NetworkInterface ni = netinf.nextElement();
			Enumeration<InetAddress> addr = ni.getInetAddresses();
			while(addr.hasMoreElements()) {
				InetAddress ia = addr.nextElement();
				String ipa = ia.getHostAddress();
				if(ipa.equals(ip)) {
					return true;
				}
//				System.out.println("\tisAnyLocalAddress: "+ia.isAnyLocalAddress());
//				System.out.println("\tisLinkLocalAddress: "+ia.isLinkLocalAddress());
//				System.out.println("\tisLoopbackAddress: "+ia.isLoopbackAddress());
//				System.out.println("\tisMulticastAddress: "+ia.isMulticastAddress());
//				System.out.println("\tisSiteLocalAddress: "+ia.isSiteLocalAddress());
			}
		}
		return false;
	}
}
