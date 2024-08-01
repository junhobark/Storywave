package com.ormi.storywave.posts;

import com.ormi.storywave.comment.CommentDto;
import com.ormi.storywave.comment.CommentRepository;
import com.ormi.storywave.users.UserRepository;
import com.ormi.storywave.users.UsersDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/home")
public class PostController {
  private final PostsService postsService;
  private final UserRepository userRepository;
  private final CommentRepository commentRepository;

  @Autowired
  public PostController(PostsService postsService, UserRepository userRepository, CommentRepository commentRepository) {
    this.postsService = postsService;
    this.userRepository = userRepository;
    this.commentRepository = commentRepository;
  }

  @GetMapping("/post/login")
  @ResponseBody
  public String login(Model model) {
    return "board/로그인 화면 파일 이름";
  }

  // 게시물 상세화면 조회
  @GetMapping("/post/{postId}")
  public String postsDetail(
          @PathVariable("postId") Integer postId, @RequestParam("userId") String userId, Model model) {
    System.out.println("postsDetail 실행");
    PostDto posts =
            postsService
                    .getPostById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 포스트는 존재하지 않습니다."));

    UsersDto users =
            userRepository
                    .findById(userId)
                    .map(UsersDto::fromUsers)
                    .orElseThrow(() -> new IllegalArgumentException("해당 유저는 존재하지 않습니다."));

    String writer = userRepository.findByPosts_PostId(postId)
            .map(UsersDto::fromUsers)
            .map(UsersDto::getNickname)
            .orElseThrow(() -> new IllegalArgumentException("해당 포스트의 작성자를 찾을 수 없습니다."));

    String role = users.getRole();

    model.addAttribute("writer", writer); // 글쓴이

    model.addAttribute("posts", posts);

    model.addAttribute("users", users);

    model.addAttribute("role", role); // 권한

    model.addAttribute("comments", posts.getCommentsList().size()); // 댓글 개수

    return "board/posts_detail";
  }

  @GetMapping("/mypage/mypost")
  public String getAllPosts(
          Model model, @RequestParam(value = "page", defaultValue = "1") int page) {
    int pageSize = 10; // 한 페이지에 보여줄 게시글 수
    Page<Posts> postPage = postsService.findPaginated(page, pageSize);

    // 모델에 데이터를 추가하여 뷰에 전달
    model.addAttribute("posts", postPage.getContent()); // 현재 페이지 게시물 리스트
    model.addAttribute("currentPage", page); // 현재 페이지 번호
    model.addAttribute("totalPages", postPage.getTotalPages()); // 총 페이지 수
    return "mypage/mypost"; // myPage_myPost.html 템플릿을 반환
  }

  // /posts 경로로 POST 요청을 처리하여 새로운 게시물 생성
//  @PostMapping("/posts")
//  @ResponseBody
//  public Posts createPost(@RequestBody Posts post) {
//    return postsService.createPost(post); // 새로운 게시물 생성 후 반환
//  }
}