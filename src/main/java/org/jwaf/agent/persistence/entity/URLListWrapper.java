package org.jwaf.agent.persistence.entity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

public class URLListWrapper implements List<URL>
{
	private List<String> wrappedList;

	public URLListWrapper(List<String> wrappedList)
	{
		this.wrappedList = wrappedList;
	}

	@Override
	public int size()
	{
		return wrappedList.size();
	}

	@Override
	public boolean isEmpty()
	{
		return wrappedList.isEmpty();
	}

	@Override
	public boolean contains(Object o)
	{
		return wrappedList.contains(o.toString());
	}

	@Override
	public Iterator<URL> iterator()
	{
		return new URLIterratorWrapper(wrappedList.iterator());
	}

	@Override
	public Object[] toArray()
	{
		return getNewURLList(wrappedList).toArray();
	}

	@Override
	public <T> T[] toArray(T[] a)
	{
		return getNewURLList(wrappedList).toArray(a);
	}

	@Override
	public boolean add(URL e)
	{
		return wrappedList.add(e.toString());
	}

	@Override
	public boolean remove(Object o)
	{
		return wrappedList.remove(((URL)o).toString());
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		return wrappedList.containsAll(getNewStringList(c));
	}

	@Override
	public boolean addAll(Collection<? extends URL> c)
	{
		return wrappedList.addAll(getNewStringList(c));
	}

	@Override
	public boolean addAll(int index, Collection<? extends URL> c)
	{
		return wrappedList.addAll(index, getNewStringList(c));
	}

	@Override
	public boolean removeAll(Collection<?> c)
	{
		return wrappedList.removeAll(getNewStringList(c));
	}

	@Override
	public boolean retainAll(Collection<?> c)
	{
		return wrappedList.retainAll(getNewStringList(c));
	}

	@Override
	public void clear()
	{
		wrappedList.clear();
	}

	@Override
	public URL get(int index)
	{
		return getNewURL((wrappedList.get(index)));
	}

	@Override
	public URL set(int index, URL element)
	{
		return getNewURL(wrappedList.set(index, element.toString()));
	}

	@Override
	public void add(int index, URL element)
	{
		wrappedList.add(index, element.toString());
	}

	@Override
	public URL remove(int index)
	{
		return getNewURL(wrappedList.remove(index));
	}

	@Override
	public int indexOf(Object o)
	{
		return wrappedList.indexOf(o.toString());
	}

	@Override
	public int lastIndexOf(Object o)
	{
		return wrappedList.lastIndexOf(o.toString());
	}

	@Override
	public ListIterator<URL> listIterator()
	{
		return new URLListIterratorWrapper(wrappedList.listIterator());
	}

	@Override
	public ListIterator<URL> listIterator(int index)
	{
		return new URLListIterratorWrapper(wrappedList.listIterator(index));
	}

	@Override
	public List<URL> subList(int fromIndex, int toIndex)
	{
		return getNewURLList(wrappedList.subList(fromIndex, toIndex));
	}

	private URL getNewURL(String s)
	{
		try
		{
			return new URL(s);
		} 
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private List<String> getNewStringList(Collection<?> c)
	{
		return c.stream().map((url)-> url.toString()).collect(Collectors.toList());
	}

	private List<URL> getNewURLList(List<String> strList)
	{
		return strList.stream().map((str)-> getNewURL(str)).collect(Collectors.toList());
	}

	protected class URLIterratorWrapper implements Iterator<URL>
	{
		private Iterator<String> wrappedIterrator;
		
		public URLIterratorWrapper(Iterator<String> wrappedIterrator)
		{
			this.wrappedIterrator = wrappedIterrator;
		}
		
		@Override
		public URL next()
		{
			return getNewURL(wrappedIterrator.next());
		}

		@Override
		public boolean hasNext()
		{
			return wrappedIterrator.hasNext();
		}
	}
	
	protected class URLListIterratorWrapper implements ListIterator<URL>
	{
		private ListIterator<String> wrappedIterrator;
		
		public URLListIterratorWrapper(ListIterator<String> wrappedIterrator)
		{
			this.wrappedIterrator = wrappedIterrator;
		}
		
		@Override
		public boolean hasNext()
		{
			return wrappedIterrator.hasNext();
		}

		@Override
		public URL next()
		{
			return getNewURL(wrappedIterrator.next());
		}

		@Override
		public boolean hasPrevious()
		{
			return wrappedIterrator.hasPrevious();
		}

		@Override
		public URL previous()
		{
			return  getNewURL(wrappedIterrator.previous());
		}

		@Override
		public int nextIndex()
		{
			return wrappedIterrator.nextIndex();
		}

		@Override
		public int previousIndex()
		{
			return wrappedIterrator.previousIndex();
		}

		@Override
		public void remove()
		{
			wrappedIterrator.remove();
		}

		@Override
		public void set(URL e)
		{
			wrappedIterrator.set(e.toString());
		}

		@Override
		public void add(URL e)
		{
			wrappedIterrator.add(e.toString());
		}
	}
}
