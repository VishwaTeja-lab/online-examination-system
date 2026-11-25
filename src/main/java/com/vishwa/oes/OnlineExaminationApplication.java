package com.vishwa.oes;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.*;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.*;
@SpringBootApplication
public class OnlineExaminationApplication {
 public static void main(String[] args){SpringApplication.run(OnlineExaminationApplication.class,args);}
}
@Entity class Exam {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
 String title; String description; int duration=30; boolean active=true;
 @OneToMany(mappedBy="exam",cascade=CascadeType.ALL,fetch=FetchType.EAGER,orphanRemoval=true) List<Question> questions=new ArrayList<>();
 public Long getId(){return id;} public String getTitle(){return title;} public String getDescription(){return description;} public int getDuration(){return duration;} public boolean isActive(){return active;} public List<Question> getQuestions(){return questions;}
}
@Entity class Question {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
 String prompt; String a; String b; String c; String d; String answer;
 @ManyToOne Exam exam;
 public Long getId(){return id;} public String getPrompt(){return prompt;} public String getA(){return a;} public String getB(){return b;} public String getC(){return c;} public String getD(){return d;}
}
@Entity class Submission {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
 String student; String email; String examTitle; int score; int total; LocalDateTime submitted=LocalDateTime.now();
 Submission(){} Submission(String n,String e,String t,int s,int z){student=n;email=e;examTitle=t;score=s;total=z;}
 public String getStudent(){return student;} public String getEmail(){return email;} public String getExamTitle(){return examTitle;} public int getScore(){return score;} public int getTotal(){return total;} public LocalDateTime getSubmitted(){return submitted;}
}
interface Exams extends JpaRepository<Exam,Long>{}
interface Submissions extends JpaRepository<Submission,Long>{List<Submission> findByEmailOrderBySubmittedDesc(String email);}
@Controller class Pages {
 final Exams exams; final Submissions submissions;
 Pages(Exams e,Submissions s){exams=e;submissions=s;}
 @GetMapping("/") String home(Model m,HttpSession s){m.addAttribute("exams",exams.findAll().stream().filter(x->x.active).toList());m.addAttribute("student",s.getAttribute("student"));return "index";}
 @PostMapping("/login") String login(@RequestParam String name,@RequestParam String email,HttpSession s){if(name.isBlank()||!email.contains("@"))return "redirect:/";s.setAttribute("student",name);s.setAttribute("email",email.toLowerCase());return "redirect:/";}
 @PostMapping("/logout") String logout(HttpSession s){s.invalidate();return "redirect:/";}
 @GetMapping("/exam/{id}") String exam(@PathVariable Long id,Model m,HttpSession s){if(s.getAttribute("student")==null)return "redirect:/";m.addAttribute("exam",exams.findById(id).orElseThrow());return "exam";}
 @PostMapping("/exam/{id}/submit") String submit(@PathVariable Long id,@RequestParam Map<String,String> a,HttpSession s,Model m){if(s.getAttribute("student")==null)return "redirect:/";Exam e=exams.findById(id).orElseThrow();int score=0;for(Question q:e.questions)if(q.answer!=null&&q.answer.equals(a.get("q"+q.id)))score++;Submission r=submissions.save(new Submission((String)s.getAttribute("student"),(String)s.getAttribute("email"),e.title,score,e.questions.size()));m.addAttribute("result",r);return "result";}
 @GetMapping("/results") String results(Model m,HttpSession s){if(s.getAttribute("student")==null)return "redirect:/";m.addAttribute("results",submissions.findByEmailOrderBySubmittedDesc((String)s.getAttribute("email")));return "results";}
 @GetMapping("/admin") String admin(Model m){m.addAttribute("exams",exams.findAll());m.addAttribute("results",submissions.findAll());return "admin";}
 @PostMapping("/admin/create") String create(@RequestParam String title,@RequestParam(defaultValue="") String description,@RequestParam(defaultValue="30") int duration){Exam e=new Exam();e.title=title;e.description=description;e.duration=Math.max(1,duration);exams.save(e);return "redirect:/admin";}
 @PostMapping("/admin/{id}/question") String question(@PathVariable Long id,@RequestParam String prompt,@RequestParam String a,@RequestParam String b,@RequestParam String c,@RequestParam String d,@RequestParam String answer){Exam e=exams.findById(id).orElseThrow();Question q=new Question();q.prompt=prompt;q.a=a;q.b=b;q.c=c;q.d=d;q.answer=answer;q.exam=e;e.questions.add(q);exams.save(e);return "redirect:/admin";}
 @PostMapping("/admin/{id}/toggle") String toggle(@PathVariable Long id){Exam e=exams.findById(id).orElseThrow();e.active=!e.active;exams.save(e);return "redirect:/admin";}
 @PostMapping("/admin/{id}/delete") String delete(@PathVariable Long id){exams.deleteById(id);return "redirect:/admin";}
}