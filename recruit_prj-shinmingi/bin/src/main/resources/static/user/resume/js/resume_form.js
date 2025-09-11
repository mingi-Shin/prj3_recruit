$(function() {
	// 자동저장 관련 변수
	let autoSaveTimer = null;
	let lastActivityTime = Date.now();
	//const AUTO_SAVE_INTERVAL = 5 * 60 * 1000; // 5분
	const AUTO_SAVE_INTERVAL = 10 * 1000; //10초

	// 자동저장 알림 표시
	function showAutoSaveNotification() {
		// 기존 알림이 있으면 제거
		$('.auto-save-notification').remove();
		
		// 알림 요소 생성
		const notification = $(`
			<div class="auto-save-notification">
				<i class="fas fa-check-circle"></i>
				자동저장 완료
			</div>
		`);
		
		// body에 추가
		$('body').append(notification);
		
		// 애니메이션으로 표시
		setTimeout(() => {
			notification.addClass('show');
		}, 100);
		
		// 3초 후 사라짐
		setTimeout(() => {
			notification.removeClass('show');
			setTimeout(() => {
				notification.remove();
			}, 300);
		}, 3000);
	}

	// 자동저장 실행 함수
	function performAutoSave() {
		const formData = saveResume();

		$.ajax({
			url: '/user/resume/resumeSubmit',
			type: 'POST',
			data: formData,
			processData: false,
			contentType: false,
			success: function(response) {
				if (response.result === 'success') {
					showAutoSaveNotification();
				}
			},
			error: function(error) {
				console.error('자동저장 실패:', error);
			}
		});
	}

	// 사용자 활동 감지 및 자동저장 타이머 설정
	function resetAutoSaveTimer() {
		lastActivityTime = Date.now();
		
		// 기존 타이머 클리어
		if (autoSaveTimer) {
			clearTimeout(autoSaveTimer);
		}
		
		// 새로운 타이머 설정
		autoSaveTimer = setTimeout(() => {
			// 마지막 활동으로부터 정확히 5분이 지났는지 확인
			const timeSinceLastActivity = Date.now() - lastActivityTime;
			if (timeSinceLastActivity >= AUTO_SAVE_INTERVAL) {
				performAutoSave();
			}
		}, AUTO_SAVE_INTERVAL);
	}

	// 사용자 활동 감지 이벤트들
	$(document).on('input change keyup click', 'input, textarea, select', function() {
		resetAutoSaveTimer();
	});

	// 초기 타이머 설정
	resetAutoSaveTimer();

	/** 사진 업로드 초기화 */
	const resetPhotoUploadBox = () => {
		$('#photo-upload-box')
			.html(`
        <div class="photo-upload-text">
          <i class="fas fa-camera"></i>
          <div>사진 업로드</div>
          <small>클릭하여 선택</small>
        </div>
        <button type="button" class="photo-remove-btn" id="photo-remove-btn">×</button>
      `)
			.removeClass('has-photo');
		$('#photo-input').val('');
	};

	// 사진 업로드 클릭 시 파일 선택창 열기
	$('#photo-upload-box').on('click', function(e) {
		// 클릭된 요소가 업로드 박스 자체 또는 자식 중 삭제버튼이 아닌 경우에만 파일 선택창 열기
		if (!$(e.target).is('#photo-remove-btn')) {
			$('#photo-input').click();
		}
	});

	// 사진 업로드 후 미리보기 표시
	$('#photo-input').on('change', function() {
		const file = this.files[0];
		if (!file) return;

		const reader = new FileReader();
		reader.onload = e => {
			$('#photo-upload-box')
				.html(`<img src="${e.target.result}" alt="프로필 사진">
               <button type="button" class="photo-remove-btn" id="photo-remove-btn">×</button>`)
				.addClass('has-photo');
		};
		reader.readAsDataURL(file);
	});

	// 사진 삭제 (이벤트 위임)
	$(document).on('click', '#photo-remove-btn', e => {
		e.stopPropagation();
		resetPhotoUploadBox();
	});

	//직무 클릭시 리스트 열기
	$("#positionSelect").on("click", function() {
		$("#optionsList").toggle();
	});

	//직무 클릭시 - 이벤트 위임으로 수정
	$(document).on('click', '#optionsList li', function() {
		let position = $(this).data("value");
		let text = $(this).text();

		// 선택된 직무 개수가 5개 이상이면 추가 막기
		const currentCount = $('#positions .tag').length;
		if (currentCount >= 5) {
			alert("희망 직무는 최대 5개까지만 선택할 수 있습니다.");
			return;
		}

		// 중복 체크
		let isDuplicate = false;
		$('#positions .tag').each(function() {
			if ($(this).data('value') == position) {
				isDuplicate = true;
				return false;
			}
		});

		if (!isDuplicate) {
			$('#positions').append(
				`<span class="tag" data-value="${position}">${text}
	             <button type="button" class="remove-tag">×</button>
	          	 </span>`
			);
		}

		$("#optionsList").hide();
	});


	//외부 클릭시 직무 리스트 닫기
	$(document).on("click", function(e) {
		if (!$(e.target).closest("#positionSelect").length) {
			$("#optionsList").hide();
		}
	});

	// 기술스택 입력 처리
	$("#skill-input").on("keyup", function() {
		let keyword = $(this).val();

		if (keyword === "") {
			// 입력이 비어있으면 목록 숨기고 종료
			$('#skillsList').empty();
			$("#skillsList").hide();
			return;
		}

		$.ajax({
			url: "/techStack/search",
			type: 'GET',
			data: { keyword: keyword },
			success: function(response) {
				$('#skillsList').empty();
				response.forEach(item => {
					$('#skillsList').append(`<li data-value="${item.techStackSeq}">${item.stackName}</li>`);
					if (response.length > 0) {
						$("#skillsList").show();
					} else {
						$("#skillsList").hide();
					}
				});
			},
			error: function(error) {
				console.error('오류:', error);
				alert('검색 중 오류 발생');
			}
		});

	});

	//검색된 기술 스택 클릭시
	$('#skillsList').on('click', 'li', function() {
		let skill = $(this).data("value");
		let text = $(this).text();

		// 중복 체크: 이미 같은 기술스택이 있는지 확인
		let isDuplicate = false;
		$('#skills .tag').each(function() {
			if ($(this).data('value') == skill) {
				isDuplicate = true;
				return false; // each 루프 종료
			}
		});

		// 중복이 아닌 경우에만 추가
		if (!isDuplicate) {
			$('#skills').append(
				`<span class="tag" data-value="${skill}">${text}
		            <button type="button" class="remove-tag">×</button>
		         </span>`
			);
		}

		// 검색 목록 숨기기 및 입력창 초기화
		$('#skillsList').hide();
		$('#skill-input').val('');
	});

	//외부 클릭시 기술 스택 리스트 닫기
	$(document).on("click", function(e) {
		if (!$(e.target).closest("#skillsSelect").length) {
			$("#skillsList").hide();
		}
	});

	//프로젝트 기술스택 입력 처리
	$(document).on("keyup", ".project-input", function() {
		const keyword = $(this).val();
		const $projectItem = $(this).closest(".project-item");
		const $list = $projectItem.find(".project-list");

		if (keyword === "") {
			$list.empty().hide();
			return;
		}

		$.ajax({
			url: "/techStack/search",
			type: 'GET',
			data: { keyword: keyword },
			success: function(response) {
				$list.empty();
				if (response.length > 0) {
					response.forEach(item => {
						$list.append(`<li data-value="${item.techStackSeq}">${item.stackName}</li>`);
					});
					$list.show();
				} else {
					$list.hide();
				}
			},
			error: function(error) {
				console.error('오류:', error);
				alert('검색 중 오류 발생');
			}
		});
	});


	//검색된 기술 스택 클릭시
	$(document).on('click', '.project-list li', function() {
		const $projectItem = $(this).closest(".project-item");
		const skill = $(this).data("value");
		const text = $(this).text();

		// 해당 프로젝트 내에서 중복 체크
		let isDuplicate = false;
		$projectItem.find('.project-skills .tag').each(function() {
			if ($(this).data('value') == skill) {
				isDuplicate = true;
				return false; // each 루프 종료
			}
		});

		// 중복이 아닌 경우에만 추가
		if (!isDuplicate) {
			$projectItem.find('.project-skills').append(
				`<span class="tag" data-value="${skill}">${text}
		       <button type="button" class="remove-tag">×</button>
		     </span>`
			);
		}

		// 검색 목록 숨기기 및 입력창 초기화
		$projectItem.find('.project-list').hide();
		$projectItem.find('.project-input').val('');
	});

	//외부 클릭시 기술 스택 리스트 닫기
	$(document).on("click", function(e) {
		$(".project-select").each(function() {
			if (!$(e.target).closest(this).length) {
				$(this).find(".project-list").hide();
			}
		});
	});


	// 기술&포지션 태그 삭제 (이벤트 위임)
	$(document).on('click', '.remove-tag', function() {
		$(this).parent().remove();
	});


	/** 학력/경력/프로젝트/기타 공통 추가 함수 */
	const addItem = (containerSelector, html) => $(containerSelector).append(html);

	// 각 추가 버튼에 맞게 아이템 추가
	$('#add-edu').on('click', () => addItem('.edu-list', eduHtml()));
	$('#add-exp').on('click', () => addItem('.exp-list', expHtml()));
	$('#add-proj').on('click', () => addItem('.proj-list', projHtml()));
	$('#add-etc').on('click', () => addItem('.etc-list', etcHtml()));
	$('#add-intro').on('click', () => addItem('.intro-list', introHtml()));

	// 각 항목별 HTML 템플릿 함수 (필요한 항목 추가 시 유지보수 용이)
	const eduHtml = () => `
	<div class="item-box">
	  <button type="button" class="item-remove-btn">×</button>
	  <div class="row g-3">
	    <div class="col-md-2"><label class="form-label">입학 날짜</label><input type="text" class="form-control" name="admissionDate" placeholder="YYYY.MM"></div>
	    <div class="col-md-2"><label class="form-label">졸업날짜(예정)</label><input type="text" class="form-control" name="graduateDate" placeholder="YYYY.MM"></div>
	    <div class="col-md-4"><label class="form-label">학교명</label><input type="text" class="form-control" name="schoolName" placeholder="○○대학교"></div>
	    <div class="col-md-4"><label class="form-label">학과</label><input type="text" class="form-control" name="department" placeholder="컴퓨터공학과"></div>
	    <div class="col-md-2"><label class="form-label">학력구분</label><select class="form-select" name="educationType"><option selected disabled>선택해주세요</option><option>고등학교</option><option>대학(2,3년)</option><option>대학(4년)</option><option>대학원(석사)</option><option>대학원(박사)</option></select></div>
	    <div class="col-md-2"><label class="form-label">학점</label><input type="text" class="form-control" name="grade" placeholder="예: 3.5"></div>
	    <div class="col-md-2"><label class="form-label">기준학점</label><select class="form-select" name="standardGrade"><option selected disabled>선택해주세요</option><option>4.5</option><option>4.3</option><option>4.0</option><option>5.0</option><option>7.0</option><option>100</option></select></div>
	  </div>
	</div>
	`;

	const expHtml = () => `
    <div class="item-box">
      <button type="button" class="item-remove-btn">×</button>
      <div class="row g-3">
        <div class="col-md-2"><label class="form-label">입사년월</label><input type="text" class="form-control" name="startDate" placeholder="YYYY.MM"></div>
        <div class="col-md-2"><label class="form-label">퇴사년월</label><input type="text" class="form-control" name="endDate" placeholder="YYYY.MM"></div>
        <div class="col-md-4"><label class="form-label">회사명</label><input type="text" class="form-control" name="companyName" placeholder="(주)○○회사"></div>
        <div class="col-md-4"><label class="form-label">직책/직무</label><input type="text" class="form-control" name="position" placeholder="주임/백엔드 개발자"></div>
        <div class="col-12"><label class="form-label">담당업무 및 성과</label><textarea class="form-control" rows="3" name="carrerDescription"placeholder="담당했던 업무와 성과를 구체적으로 입력해주세요"></textarea></div>
      </div>
      </div>`;

	const projHtml = () => `
	    <div class="item-box project-item">
	      <button type="button" class="item-remove-btn">×</button>
	      <div class="row g-3">
	        <div class="col-md-2"><label class="form-label">시작일</label><input type="text" class="form-control" name="startDate" placeholder="YYYY.MM"></div>
	        <div class="col-md-2"><label class="form-label">종료일</label><input type="text" class="form-control" name="endDate" placeholder="YYYY.MM"></div>
	        <div class="col-md-6"><label class="form-label">프로젝트명</label><input type="text" class="form-control" name="projectName" placeholder="쇼핑몰 웹사이트 개발"></div>
	        <div class="project-skills mt-3"></div>
	        <div class="custom-select project-select">
	          <input type="text" class="form-control project-input" placeholder="프로젝트에서 사용한 기술스택을 입력해주세요.">
	          <ul class="custom-options project-list"></ul>
	        </div>
	        <div class="col-12"><label class="form-label">상세 내용</label><textarea class="form-control" rows="4" name="projectContent" placeholder="프로젝트에서 담당한 역할과 구현한 기능을 상세히 설명해주세요"></textarea></div>
	        <div class="col-md-6"><div class="form-check mt-3"><input class="form-check-input" name="releaseStatus" type="checkbox"><label class="form-check-label">배포/출시 완료</label></div></div>
	        <div class="col-md-6"><label class="form-label">저장소 링크</label><input type="url" class="form-control" name="repositoryLink" placeholder="https://github.com/username/project"></div>
	      </div>
	    </div>
	  `;

	const etcHtml = () => `
    <div class="item-box">
      <button type="button" class="item-remove-btn">×</button>
      <div class="row g-3">
        <div class="col-md-2"><label class="form-label">취득일/종료일</label><input type="text" class="form-control" name="endDate" placeholder="YYYY.MM"></div>
        <div class="col-md-4"><label class="form-label">활동명/자격증명</label><input type="text" class="form-control" name="activityName" placeholder="정보처리기사"></div>
        <div class="col-md-2"><label class="form-label">이력 구분</label><select class="form-select" name="recordType"><option selected disabled>선택해주세요</option><option>자격증</option><option>수상이럭</option><option>대외활동</option><option>어학</option></select></div>
        <div class="col-md-4"><label class="form-label">관련 기관</label><input type="text" class="form-control" name="relatedAgency" placeholder="한국산업인력공단"></div>
        <div class="col-12"><label class="form-label">상세 내용</label><textarea class="form-control" rows="3" name="detailContent" placeholder="자세한 내용이나 성과를 입력해주세요"></textarea></div>
      </div>
    </div>`;

	const introHtml = () => `
	<div class="item-box">
	<button type="button" class="item-remove-btn">×</button>
	<div class="mb-3"><label class="form-label">제목</label> <input type="text"class="form-control" name="title" placeholder="자기소개서 제목을 입력해주세요"></div>
	<div class="mb-3"><label class="form-label">내용</label><textarea class="form-control" rows="8" name="content" placeholder="자기소개 내용을 입력해주세요"></textarea></div>
	</div>
	`

	// 공통 삭제 버튼 처리 (이벤트 위임)
	$(document).on('click', '.item-remove-btn', function() {
		$(this).parent().remove();
	});

	// 날짜 형식 입력 처리 (YYYY.MM)
	$(document).on('input', 'input[placeholder="YYYY.MM"]', function() {
		let value = this.value.replace(/\D/g, '');
		this.value = value.length >= 4 ? value.substring(0, 4) + '.' + value.substring(4, 6) : value;
	});

	// 학점 형식 입력 처리
	$(document).on('input', 'input[name="grade"]', function() {
		let value = this.value.replace(/[^\d.]/g, '');
		let parts = value.split('.');

		// 소수점 중복 제거 및 자릿수 제한
		if (parts.length > 2) parts = [parts[0], parts[1]];
		parts[0] = parts[0].substring(0, 1);
		if (parts[1]) parts[1] = parts[1].substring(0, 2);

		// 정수 1개 입력시 자동으로 소수점 추가
		if (parts[0].length === 1 && parts.length === 1) {
			this.value = parts[0] + '.';
		} else {
			this.value = parts[1] !== undefined ? parts[0] + '.' + parts[1] : parts[0];
		}
	});

	// 백스페이스로 소수점 지우기 처리
	$(document).on('keydown', 'input[name="grade"]', function(e) {
		if (e.key === 'Backspace' && this.value.endsWith('.') && this.value.length === 2) {
			this.value = this.value.substring(0, 1);
			e.preventDefault();
		}
	});

	$(document).on('keydown', 'input[placeholder="YYYY.MM"]', function(e) {
		if (e.key === 'Backspace' && this.value.endsWith('.') && this.value.length === 5) {
			this.value = this.value.substring(0, 4);
			e.preventDefault();
		}
	});

	// 간단한 월 검증 (1~12월만 체크)
	$(document).on('blur', 'input[placeholder="YYYY.MM"]', function() {
		const dateValue = $(this).val();

		if (dateValue && dateValue.includes('.')) {
			const parts = dateValue.split('.');
			if (parts.length === 2) {
				const month = parseInt(parts[1]);

				if (parts[1] && (month < 1 || month > 12)) {
					alert('월은 01부터 12까지만 입력 가능합니다.');
					$(this).val(parts[0] + '.');
					$(this).focus();
				}
			}
		}
	});
	
	$(document).on('click', function(e) {
	  if (!$(e.target).closest('textarea').length) {
	    $('textarea').blur(); // textarea 포커스 해제
	  }
	});

	// ===============================
	// 이력서 데이터 수집 및 전송 함수들
	// ===============================

	// 이력서 데이터 수집 함수
	function collectResumeData() {
		//데이턱 저장하는 곳
		const resumeData = {
			basicInfo: {
				resumeSeq: $('#resumeSeq').val(),
				title: $('input[name="title"]').val(),
				introduction: $('textarea[name="introduction"]').val(),
				isPublic: $('.privacy-toggle input[type="checkbox"]').is(':checked')
			},
			links: {
				githubUrl: $('.links-section .fa-github').parent().next('input').val(),
				notionUrl: $('.links-section .fa-file-alt').parent().next('input').val(),
				blogUrl: $('.links-section .fa-blog').parent().next('input').val()
			},
			positions: [], skills: [], educations: [], careers: [], projects: [], projectSkills: [], etc: [], introductions: []
		};

		// 희망 직무 수집
		$('#positions .tag').each(function() {
			const positionSeq = $(this).data('value');
			if (positionSeq) {
				resumeData.positions.push({ positionSeq: positionSeq });
			}
		});

		// 기술스택 수집
		$('#skills .tag').each(function() {
			const techStackSeq = $(this).data('value');
			if (techStackSeq) {
				resumeData.skills.push({ techStackSeq: techStackSeq });
			}
		});

		// 학력 수집
		$('.edu-list .item-box').each(function() {
			const education = {
				admissionDate: $(this).find('input[name="admissionDate"]').val(),
				graduateDate: $(this).find('input[name="graduateDate"]').val(),
				schoolName: $(this).find('input[name="schoolName"]').val(),
				department: $(this).find('input[name="department"]').val(),
				educationType: $(this).find('select[name="educationType"]').val(),
				grade: $(this).find('input[placeholder="예: 3.5"]').val(),
				standardGrade: $(this).find('select[name="standardGrade"]').val()
			};

			// 빈 값이 아닌 경우만 추가
			if (education.schoolName || education.department) {
				resumeData.educations.push(education);
			}
		});

		// 경력 수집
		$('.exp-list .item-box').each(function() {
			const career = {
				startDate: $(this).find('input[name="startDate"]').val(),
				endDate: $(this).find('input[name="endDate"]').val(),
				companyName: $(this).find('input[name="companyName"]').val(),
				position: $(this).find('input[name="position"]').val(),
				careerDescription: $(this).find('textarea').val()
			};

			if (career.companyName || career.position) {
				resumeData.careers.push(career);
			}
		});

		// 프로젝트 수집
		$('.proj-list .item-box').each(function() {
			const projectSkills = [];
			$(this).find('.project-skills .tag').each(function() {
				const techStackSeq = $(this).data('value');
				if (techStackSeq) {
					projectSkills.push({ techStackSeq: techStackSeq });
				}
			});

			const project = {
				startDate: $(this).find('input[name="startDate"]').val(),
				endDate: $(this).find('input[name="endDate"]').val(),
				projectName: $(this).find('input[name="projectName"]').val(),
				projectContent: $(this).find('textarea').val(),
				releaseStatus: $(this).find('.form-check-input').is(':checked'),
				repositoryLink: $(this).find('input[type="url"]').val()
			};

			if (project.projectName && projectSkills.length > 0) {
				resumeData.projects.push(project);
				resumeData.projectSkills.push(projectSkills);
			} else {
				alert('프로젝트명과 기술스택을 모두 입력해야 저장됩니다.');
			}
		});

		// 기타사항 수집
		$('.etc-list .item-box').each(function() {
			const etcItem = {
				endDate: $(this).find('input[name="endDate"]').val(),
				activityName: $(this).find('input[name="activityName"]').val(),
				recordType: $(this).find('select').val(),
				relatedAgency: $(this).find('input[name="relatedAgency"]').val(),
				detailContent: $(this).find('textarea').val()
			};

			if (etcItem.activityName) {
				resumeData.etc.push(etcItem);
			}
		});

		// 자기소개서 수집
		$('.intro-list .item-box').each(function() {
			const introduction = {
				title: $(this).find('input[name="title"]').val(),
				content: $(this).find('textarea').val()
			};

			if (introduction.title || introduction.content) {
				resumeData.introductions.push(introduction);
			}
		});

		return resumeData;
	}

	// 이력서 저장 함수
	function saveResume() {
		const resumeData = collectResumeData();
		// FormData 생성 (파일 업로드를 위해)
		const formData = new FormData();
		// 프로필 이미지가 있는 경우 추가
		const profileImageFile = $('#photo-input')[0].files[0];
		if (profileImageFile) {
			formData.append('profileImage', profileImageFile);
		}

		// 나머지 데이터는 JSON으로 변환해서 추가
		formData.append('resumeData', JSON.stringify(resumeData));

		return formData;
	}

	// 버튼 이벤트 연결
	// 이력서 저장하기
	$('.save-btn').on('click', function() {
		const formData = saveResume();

		$.ajax({
			url: '/user/resume/resumeSubmit',
			type: 'POST',
			data: formData,
			processData: false,
			contentType: false,
			success: function(response) {
				if (response.result === 'success') {
					alert('이력서가 성공적으로 저장되었습니다.');
				} else {
					alert('저장에 실패했습니다. 다시 시도해주세요.');
				}
			},
			error: function(error) {
				alert('저장 중 오류가 발생했습니다.');
				console.error('Error:', error);
			},
		});
	});

	//이력서 미리보기
	$('.preview-btn').on('click', function() {
		const formData = saveResume();

		$.ajax({
			url: '/user/resume/resumePreview',
			type: 'POST',
			data: formData,
			processData: false,
			contentType: false,
			success: function(response) {
				if (response.result === 'success') {
					window.open(response.previewUrl, '_blank', 'width=900,height=1100,scrollbars=yes,resizable=yes');
				} else {
					alert('이력서 불러오기에 실패했습니다. 다시 시도해주세요.');
				}
			},
			error: function(error) {
				alert('이력서 불러오는 중 오류가 발생했습니다.');
				console.error('Error:', error);
			}
		});
	});

	//이력서 PDF로 다운로드
	$('.download-btn').on('click', function() {
		const resumeSeq = $('#resumeSeq').val();
		location.href = '/user/resume/download/' + resumeSeq;
	});

});