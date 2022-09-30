import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;

interface IStack 
{
  public Object pop();
  
  public Object peek();
  
  public void push(Object element);
  
  public boolean isEmpty();
  
  public int size();
}


class MyStack implements IStack 
{
    int _size = 0;
    Object [] arr = new Object[1000000];
    
    public Object pop()
    {
        if (_size == 0)
        {
            System.out.print("Error");
            System.exit(0);
        }
        return arr[--_size];
    }
      
    public Object peek()
    {
        if (_size == 0)
        {
            System.out.print("Error");
            System.exit(0);
        }
        return arr[_size - 1];
    }
      
    public void push(Object element)
    {
        arr[_size++] = element; 
    }
      
    public boolean isEmpty()
    {
        return _size == 0;
    }
      
    public int size()
    {
        return _size;
    }
}

interface IExpressionEvaluator 
{
    public String infixToPostfix(String expression);
  
    public int evaluate(String expression);
}


public class Evaluator implements IExpressionEvaluator 
{ 
    int a = 0, b = 0, c = 0;
    
    String replace(String s, String pattern, String replace)
    {
        String ans = "";
        for (int i = 0; i < s.length(); i++)
        {
            if (i != s.length() - 1 && s.charAt(i) == pattern.charAt(0) && s.charAt(i + 1) == pattern.charAt(1))
            {
                ans += replace; i++;
            }
            else
            {
                ans += s.charAt(i);
            }
        }
        return ans;
    }
    boolean compare(char a, char b)
    {
        if (a == '^')
            return true;
        if (b == '^')
            return false;
        
        if ( (b == '+' || b == '-' ) && (a == '/' || a == '*' || b == '^') )
            return true;
        
        return false;
    }
    boolean isoperand(char c)
    {
        if (c == 'a' || c == 'b' || c == 'c')
            return true;
        return false;
    }
    boolean isoperator(char c)
    {
        if (c == '+' || c == '-' || c == '*' || c == '/' || c == '^')
            return true;
        return false;
    }
    boolean isbracket(char c)
    {
        if (c == '(' || c == ')')
            return true;
        return false;
    }
    boolean issign(char c)
    {
        if (c == '+' || c == '-')
            return true;
        return false;
    }
    
    void checkadjacenterror(String s)
    {
        // two operators
        for (int i = 1; i < s.length(); i++)
        {
            char first = s.charAt(i - 1), second = s.charAt(i);
            if (isoperator(second) && !issign(second))
            {
                if (isoperator(first))
                    error();
            }
            else if (issign(first))
            {
                if (isoperator(second) && !issign(second))
                    error();
            }
        }

        // two operands
        for (int i = 1; i < s.length(); i++)
        {
            char first = s.charAt(i - 1), second = s.charAt(i);
            if (isoperator(first) || isoperator(second) || first == '(' || second == ')')
                continue;
            error();
        }
        
        // operator begin or end
        //begin
        for (int i = 0; i < s.length() - 1; i++)
        {
            if (s.charAt(i) != '(')    continue;
            
            if ( isoperator(s.charAt(i + 1) ) && !issign(s.charAt(i + 1)))
                error();
        }
        if (isoperator(s.charAt(0)) && !issign(s.charAt(0)))
            error();
        //begin
        for (int i = 1; i < s.length(); i++)
        {
            if (s.charAt(i) != ')')    continue;
            
            if ( isoperator(s.charAt(i - 1) ) )
                error();
        }
        if (isoperator(s.charAt(s.length() - 1)) )
            error();
    }
    
    public void error()
    {
        System.out.print("Error");
        System.exit(0);
    }
    // You forgot 2 adjacent operands or brackets
    public String checkexpression(String s)
    {
        StringBuilder ans = new StringBuilder();
        
        ans.append(s.charAt(0) );
        for (int i = 1; i < s.length(); i++)
        {
            if (ans.length() == 0)
            {
                ans.append(s.charAt(i));
                continue;
            }
            if (s.charAt(i) == '-')
            {
                if (ans.charAt(ans.length() - 1) == '-')
                {
                    ans.setCharAt(ans.length() - 1, '+');
                    continue;
                }
                else if (ans.charAt(ans.length() - 1) == '+')
                {
                    ans.setCharAt(ans.length() - 1, '-');
                    continue;
                }
//                else if (ans.charAt(ans.length() - 1) == '+')
//                    error();
            }
            else if (s.charAt(i) == '+' && (ans.charAt(ans.length() - 1) == '+' || ans.charAt(ans.length() - 1) == '-'))
            {
                continue;
            }
            if (ans.length() != 0 || s.charAt(i) != '+')
                ans.append(s.charAt(i));
        }
        return ans.toString();
    }
    public String infixToPostfix(String expression)
    {
        expression = getfirstpositive(expression);
        expression = checkexpression(expression);
        checkadjacenterror(expression);
        
        String s = "";
        MyStack stack = new MyStack();
        
        for (int i = 0; i < expression.length(); i++)
        {
            if (expression.charAt(i) == 'a' || expression.charAt(i) == 'b' || expression.charAt(i) == 'c')
            {
                s += expression.charAt(i);
                continue;
            }
            
            if (expression.charAt(i) == '(')
            {
                stack.push('(');
                continue;
            }
            if (expression.charAt(i) == ')')
            {
                while (!stack.isEmpty() && (char)stack.peek() != '(')
                    s += stack.pop();
                if (stack.isEmpty())
                    error();
                stack.pop();
                continue;
            }
            while (!stack.isEmpty())
            {
                if ((char)stack.peek() == '(')
                    break;
                if (!compare(expression.charAt(i), (char)stack.peek()))
                    s += stack.pop();
                else
                    break;
                
            }
            stack.push(expression.charAt(i));
        }
        int brackets = 0;
        while (!stack.isEmpty())
        {
            if ((char)stack.peek() == ')')
            {
                brackets++;
            }
            else if ((char)stack.peek() == '(')
            {
                brackets--;
                if (brackets < 0)
                    error();
            }
            else
                s += stack.peek();
            stack.pop();
        }
        if (brackets != 0)
            error();
        return s;
    }
     
    public String getfirstpositive(String s)
    {
        String ans = "";
        int n = s.length();
        int index = -1;
        
        while (index < n)
        {
            int sign = 1;

            if (index != -1)
                ans = ans + s.charAt(index);
            if (index == -1 || isbracket(s.charAt(index)) )
            {
                for (int j = index + 1; j < n; j++)
                {
                    if (s.charAt(j) == '+') {}
                    else if (s.charAt(j) == '-')    { sign *= -1; }
                    else
                    {
                        index = j - 1;
                        break;
                    }
                }
                if (sign == -1)
                    ans = ans + '-';
            }
            index++;
        }
        
        return ans;
    }
    public int evaluate(String expression)
    {
        int ans = 0, n = expression.length();
        MyStack stack = new MyStack();
        for (int i = 0; i < n; i++)
        {
            if (isoperand(expression.charAt(i)))
            {
                int aa;
                if (expression.charAt(i) == 'a')
                    aa = a;
                else if (expression.charAt(i) == 'b')
                    aa = b;
                else
                    aa = c;
            
                stack.push(aa);
            }
            else
            {
                int first = (stack.size() > 0) ? (int)stack.pop() : 0;
                int second = (stack.size() > 0) ? (int)stack.pop() : 0;
                
                
                if (expression.charAt(i) == '+')
                {
                    stack.push(second + first);
                }
                else if (expression.charAt(i) == '-')
                {
                    stack.push(second - first);
                }
                else if (expression.charAt(i) == '/')
                {
                    stack.push(second / first);
                }
                else if (expression.charAt(i) == '*')
                {
                    stack.push(second * first);
                }
                else if (expression.charAt(i) == '^')
                {
                    stack.push((int)Math.pow(second, first));
                }
                
            }
        }
        
        return (stack.size() > 0) ? (int)stack.pop() : 0;
    }
    public static void main(String[] args) 
    {
        Evaluator myevaluator = new Evaluator();
        Scanner sc = new Scanner(System.in);
        
        String s = sc.nextLine();
        
        s = myevaluator.replace(s, "--", "+");
        s = myevaluator.replace(s, "/+", "/");
        s = myevaluator.replace(s, "*+", "*");
        s = myevaluator.replace(s, "^+", "^");
        
        
        
        String s1 = sc.nextLine().substring(2);
        String s2 = sc.nextLine().substring(2);
        String s3 = sc.nextLine().substring(2);
        
        myevaluator.a = Integer.parseInt(s1);
        myevaluator.b = Integer.parseInt(s2);
        myevaluator.c = Integer.parseInt(s3);
        
        String postfix = myevaluator.infixToPostfix(s);
        System.out.println(postfix);
        System.out.print(myevaluator.evaluate(postfix));
    }
}
