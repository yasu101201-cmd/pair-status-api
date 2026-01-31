package com.example.pairstatusapi;

import com.example.pairstatusapi.entity.PairEntity;
import com.example.pairstatusapi.entity.UserEntity;
import com.example.pairstatusapi.repository.PairRepository;
import com.example.pairstatusapi.repository.UserRepository;
import com.example.pairstatusapi.service.PairService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PairStatusApiApplicationTests {

	@Autowired
	private PairService pairService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PairRepository pairRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void testPairCreation() {
		// ユーザーを作成
		UUID userId = UUID.randomUUID();
		UserEntity user = new UserEntity();
		user.setId(userId);
		user.setEmail("test@example.com");
		userRepository.save(user);
		
		// ペアを作成
		PairEntity pair = pairService.create(userId);
		
		// アサーション
		assertNotNull(pair);
		assertNotNull(pair.getId());
		assertNotNull(pair.getJoinCode());
		assertEquals(userId, pair.getUserId1());
		assertNull(pair.getUserId2());
		
		// ユーザーが更新されているか確認
		UserEntity updatedUser = userRepository.findById(userId)
				.orElseThrow(() -> new AssertionError("User not found"));
		assertEquals(pair.getId(), updatedUser.getPairId());
	}

}
