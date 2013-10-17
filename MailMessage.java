/*
 *      untitled.java
 *      
 *      Copyright 2012 BeNikis <benikis@PCN>
 *      
 *      This program is free software; you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation; either version 2 of the License, or
 *      (at your option) any later version.
 *      
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *      
 *      You should have received a copy of the GNU General Public License
 *      along with this program; if not, write to the Free Software
 *      Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 *      MA 02110-1301, USA.
 */
 
import java.io.*;

public class MailMessage {
	public String from;
	public String to;
	public String subject;
	public String date;
	public String body;
	public boolean deleted;
	public int size;
	
	public MailMessage() {
		from=to=date=subject=body="";
	};
	
	public MailMessage(String message,int size) {
		fillMessage(message,size);
	};
	
	public void fillMessage (String message,int size) {
		
			
		body="";
		this.size=size;
		deleted=false;
		String[] lines = message.split("\n");
		
		for (int i=0;i<lines.length;i++) {
			String line=lines[i];
			
			if (line.matches(".*:.*")) {
				String[] field = new String[2];
				field[0]=line.substring(0,line.indexOf(":")).toLowerCase();
				field[1]=line.substring(line.indexOf(":")+1,line.length());
				
				
				if (field[0].equals("from")) from=field[1];
				else if (field[0].equals("to")) to=field[1];
				else if (field[0].equals("subject")) subject=field[1];
				else if (field[0].equals("date")) date=field[1];
			} else if (line.matches("[\t\n\f\r]*")) {
				for(int y=i+1;y<lines.length;y++)
					body+=lines[y]+"\n";
				break;
			};
		};
	};
	
	


};
