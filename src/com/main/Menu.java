package com.main;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Menu 
{
	List<MenuItem> menuItems = new ArrayList<MenuItem>();
	String strPrintFormat = "%s%2d\t%-20s\n";
	
	public void AddItem(MenuItem item)
	{
		menuItems.add(item);
	}
	
	public void Run()
	{
		Scanner sc = new Scanner(System.in);
		while(true)
		{
			try 
			{	
				MenuItem item = null;
				for(int i=0 ; i<menuItems.size() ; i++)
				{
					item = menuItems.get(i);
					System.out.printf(strPrintFormat , "????" , i+1 , item.strItemName);
				}
				System.out.printf(strPrintFormat, "????" , 0 , "??? App");
				String strUserInput = sc.next();
				int nUserInput = 0;
				try
				{
					nUserInput  = Integer.parseInt(strUserInput);
				}
				catch(NumberFormatException ex)
				{
					nUserInput = -1;
				}
				if( 0 == nUserInput )
				{
					break;
				}
				
				item =  menuItems.get(nUserInput-1);
				if( item  != null)
				{
					Class<?> testClass = item.instance.getClass();
					Method method = testClass.getMethod(item.strItemFunc);
					method.invoke(item.instance);
				}
				else
				{
					System.out.println("Input Error");
				}
		
			} 
			catch (Exception e) 
			{
				System.out.println(e);
				//e.printStackTrace();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
