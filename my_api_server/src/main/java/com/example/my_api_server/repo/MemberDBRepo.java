package com.example.my_api_server.repo;

import com.example.my_api_server.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // bean으로 등록
public interface MemberDBRepo extends JpaRepository<Member, Long> {
}
