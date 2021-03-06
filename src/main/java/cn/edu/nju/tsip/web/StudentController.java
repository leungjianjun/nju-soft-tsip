package cn.edu.nju.tsip.web;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;

import cn.edu.nju.tsip.entity.Student;
import cn.edu.nju.tsip.service.IStudentService;

@Controller
public class StudentController {
	
	private Validator validator;
	
	private static final Logger logger = LoggerFactory.getLogger(MblogController.class);
	
	private IStudentService<Student> studentService;

	public Validator getValidator() {
		return validator;
	}

	@Autowired
	public void setValidator(Validator validator) {
		this.validator = validator;
	}

	public IStudentService<Student> getStudentService() {
		return studentService;
	}

	@Autowired
	public void setStudentService(IStudentService<Student> studentService) {
		this.studentService = studentService;
	}
	
	@RequestMapping(value="/client/student/getinfo",method=RequestMethod.POST)
	public @ResponseBody Map<String, ? extends Object> getStudent(@RequestBody Map<String,Integer> param, HttpServletResponse response,HttpSession session){
		logger.info("student get info");
		Student student = studentService.find(Student.class, param.get("id"));
		if(student == null ){
			Map<String, String> failureMessages = new HashMap<String, String>();
			failureMessages.put("status", "false");
			failureMessages.put("error", "不存在该学生");
			return failureMessages;
		}else{
			Map<String,Object> result = Maps.newHashMap();
			result.put("loginName", student.getLoginName());
			result.put("realName", student.getRealName());
			result.put("role", "student");
			if( student.getHeadImg().getName() == null){
				result.put("headImg", "nohead.gif");
			}else{
				result.put("headImg", student.getHeadImg().getName());
			}
			result.put("createDate", student.getCreateDate());
			result.put("online", student.isOnline());
			result.put("loginPlace", student.getLoginPlace());
			result.put("stno", student.getStno());
			result.put("remarks", student.getRemarks());
			result.put("hobby", student.getHobby());
			result.put("talent", student.getTalent());
			result.put("sex", student.isSex());
			result.put("birthday", student.getBirthday());
			return result;
		}
	}
	
	@RequestMapping(value="/client/student/getmyinfo",method=RequestMethod.POST)
	public @ResponseBody Map<String, ? extends Object> getMy(@RequestBody Map<String,Integer> param, HttpServletResponse response,HttpSession session){
		logger.info("student get info");
		Student student = studentService.find(Student.class, (Integer) session.getAttribute("id"));
		if(student == null ){
			Map<String, String> failureMessages = new HashMap<String, String>();
			failureMessages.put("status", "false");
			failureMessages.put("error", "不存在该学生");
			return failureMessages;
		}else{
			Map<String,Object> result = Maps.newHashMap();
			result.put("loginName", student.getLoginName());
			result.put("realName", student.getRealName());
			result.put("role", "student");
			if( student.getHeadImg() == null){
				result.put("headImg", "nohead.gif");
			}else{
				result.put("headImg", student.getHeadImg().getName());
			}
			result.put("createDate", student.getCreateDate());
			result.put("online", student.isOnline());
			result.put("loginPlace", student.getLoginPlace());
			result.put("stno", student.getStno());
			result.put("remarks", student.getRemarks());
			result.put("hobby", student.getHobby());
			result.put("talent", student.getTalent());
			result.put("sex", student.isSex());
			result.put("birthday", student.getBirthday());
			return result;
		}
	}
	
	@RequestMapping(value="/client/student/changeinfo",method=RequestMethod.POST)
	public @ResponseBody Map<String, ? extends Object> changeMyinfo(@RequestBody Map<String,Object> param, HttpServletResponse response,HttpSession session) throws ParseException{
		Student student = studentService.find(Student.class, (Integer) session.getAttribute("id"));
		if(param.get("loginName")!=null){
			student.setLoginName((String) param.get("loginName"));
		}
		if(param.get("realName")!=null){
			student.setRealName((String) param.get("realName"));
		}
		if(param.get("createDate")!=null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-dd-mm HH:mm:ss");
			Date date = sdf.parse((String) param.get("createDate"));
			student.setCreateDate(date);
		}
		if(param.get("stno")!=null){
			student.setStno((Integer) param.get("stno"));
		}
		if(param.get("remarks")!=null){
			student.setRemarks((String) param.get("remarks"));
		}
		if(param.get("hobby")!=null){
			student.setHobby((String) param.get("hobby"));
		}
		if(param.get("talent")!=null){
			student.setTalent((String) param.get("talent"));
		}
		if(param.get("sex")!=null){
			student.setSex((Boolean) param.get("sex"));
		}
		if(param.get("birthday")!=null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-dd-mm");
			Date birthday = sdf.parse((String) param.get("birthday"));
			student.setBirthday(birthday);
		}
		studentService.update(student);
		return Collections.singletonMap("status", "true");
	}
	
	@RequestMapping(value="/client/student/add",method=RequestMethod.POST)
	public @ResponseBody Map<String, ? extends Object> addstudent(@RequestBody Map<String,Object> param, HttpServletResponse response,HttpSession session) throws ParseException{
		Student student = new Student();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-dd-mm");
		Date birthday = sdf.parse((String) param.get("birthday"));
		student.setBirthday(birthday);
		student.setPassword((String) param.get("password"));
		student.setLoginName((String) param.get("loginName"));
		student.setStno((Integer) param.get("stdno"));
		student.setSex((Boolean) param.get("sex"));
		studentService.create(student);
		return Collections.singletonMap("status", "true");
	}
	
	@RequestMapping(value="/client/student/search",method=RequestMethod.POST)
	public @ResponseBody Map<String, ? extends Object> searchStudent(@RequestBody Map<String,Object> param, HttpServletResponse response,HttpSession session) throws ParseException{
		List<Student> students = studentService.search((String)param.get("grade"), (String)param.get("remarks"), (String)param.get("talent"), (String)param.get("hobby"), (Boolean)param.get("sex"),(String)param.get("realname"));
		
		return param;
	}

}
