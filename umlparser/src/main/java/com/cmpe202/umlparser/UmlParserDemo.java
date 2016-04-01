package com.cmpe202.umlparser;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.TypeParameter;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.TypeDeclarationStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

public class UmlParserDemo 
{

	public static void main(String[] st) throws ParseException, IOException
	{
					File folder = new File(st[0]);
					
					List<File> listOfFiles = new ArrayList<File>(Arrays.asList(folder.listFiles()));
					Map<String, String> classNames = new HashMap<String, String>();
					List<StringBuilder> objectsList;
					Map<String,StringBuilder> classBodyMap = new HashMap<String, StringBuilder>();
					Map<String,StringBuilder> finalMap = new HashMap<String, StringBuilder>();
					Map<String,String> methodMap;
					Map<String,String> variableMap;
					Map<String,String> classMap;
					Map<String,String> interfaceMap;
					Map<String,String> onlyInterfaceMap = new HashMap<String, String>();
					Map<String,String> extendMap;
					Map<String,String> usesMap;
					Map<String,List<String>> interfaceMethodList = new HashMap<String, List<String>>();
					List<File> interfaceFileList = new ArrayList<File>();
					List<File> classFileList = new ArrayList<File>();
					String interfaceConstant = "";
					
					for(int i=0;i<listOfFiles.size();)
					{
						File currentFile = listOfFiles.get(i);
						if(currentFile.isDirectory() || !currentFile.getName().endsWith(".java"))
						{
							listOfFiles.remove(i);
						}
						else
						{
							i++;
							classNames.put(currentFile.getName().replace(".java", ""), currentFile.getName().replace(".java", ""));
						}
					}
					System.out.println("\nafter removal \n");
					for(int i=0;i<listOfFiles.size();i++)
					{
						System.out.println(""+listOfFiles.get(i).getName());
					}

					for(int i=0;i<listOfFiles.size();i++)
					{
						CompilationUnit output = JavaParser.parse(listOfFiles.get(i));
						List<TypeDeclaration> typeDeclarationList = output.getTypes();
						interfaceMap = new HashMap<String, String>();
						extendMap = new HashMap<String, String>();
						
						for(int ii=0;ii<typeDeclarationList.size();ii++)
						{
							TypeDeclaration classOrInterface = typeDeclarationList.get(ii);
							if(classOrInterface instanceof ClassOrInterfaceDeclaration)
							{
								ClassOrInterfaceDeclaration classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) classOrInterface;
								if(classOrInterfaceDeclaration.isInterface())
								{
									interfaceFileList.add(listOfFiles.get(i));
								}
								else
								{
									classFileList.add(listOfFiles.get(i));
								}
							}
						}
					}
					interfaceFileList.addAll(classFileList);
					
					
					for(int i=0;i<interfaceFileList.size();i++)
					{
						CompilationUnit output = JavaParser.parse(interfaceFileList.get(i));
						List<TypeDeclaration> typeDeclarationList = output.getTypes();
						interfaceMap = new HashMap<String, String>();
						extendMap = new HashMap<String, String>();
						boolean isInterface = false;
						
						for(int ii=0;ii<typeDeclarationList.size();ii++)
						{
							System.out.println("Class Name:"+typeDeclarationList.get(ii).getName());
							String className= typeDeclarationList.get(ii).getName();
							
							TypeDeclaration classOrInterface = typeDeclarationList.get(ii);
							
							if(classOrInterface instanceof ClassOrInterfaceDeclaration)
							{
								ClassOrInterfaceDeclaration classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) classOrInterface;
								if(classOrInterfaceDeclaration.isInterface())
								{
									className = interfaceConstant+className;
									onlyInterfaceMap.put(className, className);
									isInterface = true;
								}

								List<ClassOrInterfaceType> extendString = classOrInterfaceDeclaration.getExtends();
								List<ClassOrInterfaceType> interfaceList = classOrInterfaceDeclaration.getImplements();
								if(extendString.size()>0)
								{
									ClassOrInterfaceType extendClass = extendString.get(0);
									System.out.println("Extended class:"+extendClass.getName());
									extendMap.put(extendClass.getName().toLowerCase(), extendClass.getName());
									
								}
								if(interfaceList.size()>0)
								{
									for(int zz=0;zz<interfaceList.size();zz++)
									{
										ClassOrInterfaceType interfaceClass = interfaceList.get(zz);
										System.out.println("\n\nInterface:"+interfaceClass.getName());
										interfaceMap.put(interfaceClass.getName().toLowerCase(), interfaceClass.getName());
									}
								}
								
							}
//							//objectsList.add(className);
							methodMap = new HashMap<String, String>();
							variableMap = new HashMap<String, String>();
							classMap = new HashMap<String, String>();
							
							List<BodyDeclaration> bodyDeclarationList = typeDeclarationList.get(ii).getMembers();
							for(int j=0;j<bodyDeclarationList.size();j++)
							{
								BodyDeclaration bodyDeclaration = bodyDeclarationList.get(j);
								if(bodyDeclaration instanceof MethodDeclaration)
								{
									methodImplementation(finalMap, methodMap, variableMap, onlyInterfaceMap,
											interfaceMethodList, interfaceConstant, isInterface, className,
											bodyDeclaration, classNames);
									
								}
								else if(bodyDeclaration instanceof ConstructorDeclaration)
								{
									constructorImplementation(finalMap, methodMap, variableMap, onlyInterfaceMap,
											interfaceMethodList, interfaceConstant, isInterface, className,
											bodyDeclaration, classNames);
								}
								else if(bodyDeclaration instanceof FieldDeclaration)
								{
									FieldDeclaration fieldDeclaration = (FieldDeclaration) bodyDeclaration;
									System.out.println("Variable Name: "+fieldDeclaration.getVariables().get(0)+" of Type:"+fieldDeclaration.getType());
									List<VariableDeclarator> variables = fieldDeclaration.getVariables();
									
									String dataTypeString = fieldDeclaration.getType().toString();
									if(fieldDeclaration.getModifiers()==1 || fieldDeclaration.getModifiers()==2)
									{
									String modifierAnnotation = getAnnotation(fieldDeclaration.getModifiers());
									
									boolean isCollection =false;
									for(int vai=0;vai<variables.size();vai++)
									{
										VariableDeclarator variable = variables.get(vai);
										String[] stringCollection = dataTypeString.split("\\<");
										if(stringCollection.length>1)
										{
											//We get collection here
											dataTypeString=stringCollection[1].replace(">", "");
											isCollection =true;
										}
										boolean exclude = classNames.containsKey(dataTypeString);
										System.out.println(""+exclude);
										if(exclude)
										{
											String classInputString = modifierAnnotation+variable.getId();
											if(isCollection)
											{
												classInputString=classInputString+" 0..*";
											}
											
											classInputString=classInputString+":"+dataTypeString;
											classMap.put(variable.getId().toString().toLowerCase(), classInputString);
											
										}
										else
										{
											dataTypeString = dataTypeString.replace("[]", "(*)");
											variableMap.put(variable.getId().toString().toLowerCase(), modifierAnnotation+variable.getId()+":"+dataTypeString);
										}
										System.out.println(modifierAnnotation+variable.getId()+":"+dataTypeString);
									}
								}
							}
							}
							System.out.println("Maps");
							//Create string here
							List<String> variableList =  new ArrayList<String>(variableMap.values());
							for(int z=0;z<variableList.size();z++)
							{System.out.println(""+variableList.get(z));}
							List<String> methodList =  new ArrayList<String>(methodMap.values());
							for(int z=0;z<methodList.size();z++)
							{System.out.println(""+methodList.get(z));}
							List<String> classList = new ArrayList<String>(classMap.values());
							StringBuilder stringBuilder = new StringBuilder();;
							
							stringBuilder.append("[").append(className);
							if(variableList.size()>0)
							{
								stringBuilder.append("|");
							}
							for(int zi=0;zi<variableList.size();zi++)
							{
								stringBuilder.append(variableList.get(zi));
								if(zi<variableList.size()-1)
								{
									stringBuilder.append(";");
								}
							}
							if(methodList.size()>0)
							stringBuilder.append("|");
							for(int zi=0;zi<methodList.size();zi++)
							{
								stringBuilder.append(methodList.get(zi));
								
								if(zi<methodList.size()-1)
								{
									stringBuilder.append(";");
								}
							}
							stringBuilder.append("],");
							
							classBodyMap.put(className,stringBuilder);
							
							stringBuilder = new StringBuilder();
							for(int z=0;z<classList.size();z++)
							{
								stringBuilder = new StringBuilder();
								stringBuilder.append("[").append(className);
								stringBuilder.append("]");
								
								String classNuNam = classList.get(z);
								String[] stringCollection = classNuNam.split("\\:");
								String classFinalName = stringCollection[1].replace("]", "");
								String variableName = stringCollection[0].replace("[", "").replace("-", "");
								boolean isOppositeClassACollection =false;
								if(stringCollection[0].contains("*"))
								{
									isOppositeClassACollection = true;
								}

								stringBuilder.append(">");
								stringBuilder.append(variableName+"["+classFinalName+"]");
//								stringBuilder.append(";\n");
								stringBuilder.append("");
								if(finalMap.containsKey(classFinalName+"%"+className))
								{
									String changeString = finalMap.get(classFinalName+"%"+className).toString();
									changeString = changeString.toString().replace("]>", "]-");
									if(isOppositeClassACollection)
									{
										changeString = changeString.replaceFirst("]-", "]"+variableName+"-");
									}
									else
									{
										changeString = changeString.replaceFirst("]-", "]"+variableName+" 1-");
									}
									finalMap.put(classFinalName+"%"+className,new StringBuilder(changeString));
								}
								else
								{	
								finalMap.put(className+"%"+classFinalName, stringBuilder);
								}
							}
							
							//Interface and Inheritance implementation here
							List<String> interfaceList = new ArrayList<String>(interfaceMap.values());
							for(int zi=0;zi<interfaceList.size();zi++)
							{
								String interfaceName = interfaceList.get(zi);
								finalMap.put(interfaceName+"-"+className,new StringBuilder("["+interfaceConstant+interfaceName+"]^-.-["+className+"]"));
//								finalMap.put(interfaceName+"-"+className,new StringBuilder("["+interfaceConstant+interfaceName+"]^-.-["+className+"],"));
							}
							
							List<String> extendList = new ArrayList<String>(extendMap.values());
							for(int zi=0;zi<extendList.size();zi++)
							{
								String extendName = extendList.get(zi);
//								finalMap.put(extendName+"-"+className, new StringBuilder("["+extendName+"]^-["+className+"],"));
								finalMap.put(extendName+"-"+className, new StringBuilder("["+extendName+"]^-["+className+"]"));
							}
							
						}
					}
					
					System.out.println("Final Final Output");
					StringBuilder finalString = new StringBuilder(); 
					objectsList = new ArrayList<StringBuilder>(classBodyMap.values());
					for(int i=0;i<objectsList.size();i++)
					{
						finalString.append(objectsList.get(i));
						
					}
					objectsList = new ArrayList<StringBuilder>(finalMap.values());
					for(int i=0;i<objectsList.size();i++)
					{
						finalString.append(objectsList.get(i)).append(",");
						System.out.println(objectsList.get(i));
					}
					
					System.out.println(finalString);
					
					try{
					String urlString = "http://yuml.me/diagram/scruffy/class/"+finalString;
//					URLEncoder.encode(finalString.toString(), "UTF-8")
					 URL url = new URL(urlString);
					 
					 InputStream in = new BufferedInputStream(url.openStream());
					 ByteArrayOutputStream out = new ByteArrayOutputStream();
					 byte[] buf = new byte[1024];
					 int n = 0;
					 while (-1!=(n=in.read(buf)))
					 {
					    out.write(buf, 0, n);
					 }
					 out.close();
					 in.close();
					 byte[] response = out.toByteArray();
					 
					 FileOutputStream fos = new FileOutputStream(st[1]);
					 fos.write(response);
					 fos.close();
					 System.out.println("File created.");
					 
					}
					catch(MalformedURLException  e)
					{
						e.printStackTrace();
					}
	}

	private static void methodImplementation(Map<String, StringBuilder> finalMap, Map<String, String> methodMap,
			Map<String, String> variableMap, Map<String, String> onlyInterfaceMap,
			Map<String, List<String>> interfaceMethodList, String interfaceConstant, boolean isInterface,
			String className, BodyDeclaration bodyDeclaration, Map<String, String> classNames)
	{
		MethodDeclaration methodDeclaration = (MethodDeclaration) bodyDeclaration;
		 
		String methodName = methodDeclaration.getName();
		
		if(isInterface)
		{
			List<String> methodList =  new ArrayList<String>();
			methodList.add(methodDeclaration.getName());
			interfaceMethodList.put(className, methodList);
		}
		List<Parameter> parameters = methodDeclaration.getParameters();
		Map<String,String> methodVariableMap = new HashMap<String,String>();
		for(int zi=0;zi<parameters.size();zi++)
		{
			Parameter parameter = parameters.get(zi);
			String parameterString = parameter.getId().toString()+":"+parameter.getType();
			if (onlyInterfaceMap.containsKey(interfaceConstant+parameter.getType()))
			{
//				finalMap.put(className+"-"+parameter.getType(),new StringBuilder("["+className+"]uses"+"-.->"+"["+interfaceConstant+parameter.getType()+"],"));
				finalMap.put(className+"-"+parameter.getType(),new StringBuilder("["+className+"]uses"+"-.->"+"["+interfaceConstant+parameter.getType()+"]"));
			}
			else if(classNames.containsKey(parameter.getType()))
			{
				finalMap.put(className+"-"+parameter.getType(),new StringBuilder(className+"-"+interfaceConstant+parameter.getType()));
			}
			methodVariableMap.put(parameter.getId().toString().toLowerCase(), parameterString);
		}
		
		System.out.println("Method Name:"+methodName);
		boolean skip =false;
		if(methodDeclaration.getModifiers()==1 || methodDeclaration.getModifiers()==9)
		{
			if(methodName.startsWith("get"))
			{
				String methodNameWithoutGet = methodName.replace("get", "");
				if(methodMap.containsKey("set"+methodNameWithoutGet.toLowerCase()) && variableMap.containsKey(methodNameWithoutGet.toLowerCase()))
				{
					methodMap.remove("set"+methodNameWithoutGet.toLowerCase());
					String variableValue = variableMap.get(methodNameWithoutGet.toLowerCase());
					variableValue = variableValue.replace("-", "+");
					variableMap.put(methodNameWithoutGet.toLowerCase(), variableValue);
					skip = true;
				}
			}
			else if(methodName.startsWith("set"))
			{
				String methodNameWithoutSet = methodName.replace("set", "").toLowerCase();
				if(methodMap.containsKey("get"+methodNameWithoutSet.toLowerCase()) && variableMap.containsKey(methodNameWithoutSet.toLowerCase()))
				{
					methodMap.remove("get"+methodNameWithoutSet.toLowerCase());
					String variableValue = variableMap.get(methodNameWithoutSet.toLowerCase());
					variableValue = variableValue.replace("-", "+");
					variableMap.put(methodNameWithoutSet.toLowerCase(), variableValue);
					skip=true;
				}
			}
		
			if(!skip)
			{
				String methodMapPutString = getAnnotation(methodDeclaration.getModifiers())+methodName+"(";
				List<String> methodVariableList = new ArrayList<String>(methodVariableMap.values());
				for(int zi=0;zi<methodVariableList.size();zi++)
				{
					methodMapPutString+=methodVariableList.get(zi);
					if(zi<methodVariableList.size()-1)
					{
						methodMapPutString+=";";
					}
				}
				methodMapPutString = methodMapPutString.replace("[]", "(*)");//Remove this line if not required
				methodMapPutString +=  "):"+methodDeclaration.getType();
				methodMap.put(methodName.toLowerCase(), methodMapPutString);
			}
			
			BlockStmt blockStatement = methodDeclaration.getBody();
			if(blockStatement != null){
			List<Statement> statementList = blockStatement.getStmts();
			for(int si=0;si<statementList.size();si++)
			{
				Statement statement = statementList.get(si);
				
				if(statement instanceof ExpressionStmt)
				{
					ExpressionStmt expressionStatement = (ExpressionStmt) statement;
					Expression expression = expressionStatement.getExpression();
				    List<Node> nodes = expression.getChildrenNodes();
				    if(nodes.size()>0)
				    {
				    Node node = nodes.get(0);
				    if (onlyInterfaceMap.containsKey(interfaceConstant+node))
					{
//						finalMap.put(className+"-"+node,new StringBuilder("["+className+"]uses"+"-.->"+"["+interfaceConstant+node+"],"));
						finalMap.put(className+"-"+node,new StringBuilder("["+className+"]uses"+"-.->"+"["+interfaceConstant+node+"]"));
					}
					else if(classNames.containsKey(node))
					{
						finalMap.put(className+"-"+node,new StringBuilder(className+"-"+interfaceConstant+node));
					}
				    }
				}
			}
			}
		}
		
		
	}	
	
	private static void constructorImplementation(Map<String, StringBuilder> finalMap, Map<String, String> methodMap,
			Map<String, String> variableMap, Map<String, String> onlyInterfaceMap,
			Map<String, List<String>> interfaceMethodList, String interfaceConstant, boolean isInterface,
			String className, BodyDeclaration bodyDeclaration, Map<String, String> classNames)
	{
		ConstructorDeclaration methodDeclaration = (ConstructorDeclaration) bodyDeclaration;
		 
		String methodName = methodDeclaration.getName();
		
		if(isInterface)
		{
			List<String> methodList =  new ArrayList<String>();
			methodList.add(methodDeclaration.getName());
			interfaceMethodList.put(className, methodList);
		}
		List<Parameter> parameters = methodDeclaration.getParameters();
		Map<String,String> methodVariableMap = new HashMap<String,String>();
		for(int zi=0;zi<parameters.size();zi++)
		{
			Parameter parameter = parameters.get(zi);
			String parameterString = parameter.getId().toString()+":"+parameter.getType();
			if (onlyInterfaceMap.containsKey(interfaceConstant+parameter.getType()))
			{
//											usesMap.put(className+"-"+parameter.getType(), "");
//				finalMap.put(className+"-"+parameter.getType(),new StringBuilder("["+className+"]uses"+"-.->"+"["+interfaceConstant+parameter.getType()+"],"));
				finalMap.put(className+"-"+parameter.getType(),new StringBuilder("["+className+"]uses"+"-.->"+"["+interfaceConstant+parameter.getType()+"]"));
			}
			else if(classNames.containsKey(parameter.getType()))
			{
				finalMap.put(className+"-"+parameter.getType(),new StringBuilder(className+"-"+interfaceConstant+parameter.getType()));
			}
			methodVariableMap.put(parameter.getId().toString().toLowerCase(), parameterString);
		}
		
		System.out.println("Method Name:"+methodName);
		boolean skip =false;
		if(methodDeclaration.getModifiers()==1)
		{
			if(methodName.startsWith("get"))
			{
				String methodNameWithoutGet = methodName.replace("get", "");
				if(methodMap.containsKey("set"+methodNameWithoutGet.toLowerCase()) && variableMap.containsKey(methodNameWithoutGet.toLowerCase()))
				{
					methodMap.remove("set"+methodNameWithoutGet.toLowerCase());
					String variableValue = variableMap.get(methodNameWithoutGet.toLowerCase());
					variableValue = variableValue.replace("-", "+");
					variableMap.put(methodNameWithoutGet.toLowerCase(), variableValue);
					skip = true;
				}
			}
			else if(methodName.startsWith("set"))
			{
				String methodNameWithoutSet = methodName.replace("set", "").toLowerCase();
				if(methodMap.containsKey("get"+methodNameWithoutSet.toLowerCase()) && variableMap.containsKey(methodNameWithoutSet.toLowerCase()))
				{
					methodMap.remove("get"+methodNameWithoutSet.toLowerCase());
					String variableValue = variableMap.get(methodNameWithoutSet.toLowerCase());
					variableValue = variableValue.replace("-", "+");
					variableMap.put(methodNameWithoutSet.toLowerCase(), variableValue);
					skip=true;
				}
			}
		
			if(!skip)
			{
				String methodMapPutString = getAnnotation(methodDeclaration.getModifiers())+methodName+"(";
				List<String> methodVariableList = new ArrayList<String>(methodVariableMap.values());
				for(int zi=0;zi<methodVariableList.size();zi++)
				{
					methodMapPutString+=methodVariableList.get(zi);
					if(zi<methodVariableList.size()-1)
					{
						methodMapPutString+=";";
					}
				}
				methodMapPutString +=  ")";
				methodMap.put(methodName.toLowerCase(), methodMapPutString);
			}
			//methodList.add(getAnnotation(methodDeclaration.getModifiers())+methodName+"():"+methodDeclaration.getType());
		}
	}
	
	
	

	private static String getAnnotation(int modifiers) 
	{
		if(modifiers==2)
		return "-";
		else if(modifiers==1 || modifiers==9)
		return "+";
		else if(modifiers==4)
		return "~";
		else if(modifiers==0)
		return "";
		else
		return null;	
	}

}
