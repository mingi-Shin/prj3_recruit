document.addEventListener('DOMContentLoaded', function() {

	const postingStatus = document.querySelector('select[name="postingStatus"]');

	//페이지 진입 시 url에서 공고 가져오기
	const urlParams = new URLSearchParams(location.search);
	const selectedPostingTitle = urlParams.get('postingTitle');

	updatePostingTitle(postingStatus.value, selectedPostingTitle);

	//옵션 선택시 함수 실행
	postingStatus.addEventListener('change', function() {
		updatePostingTitle(this.value);
	});

	//페이지네이션 버튼 클릭시
	document.querySelectorAll('.page-btn[data-page]').forEach(button => {
		button.addEventListener('click', function() {
			const clickPage = this.getAttribute('data-page');

			movePage(clickPage);
		})
	});

	// 이전 버튼 클릭
	document.querySelector('.prev').addEventListener('click', function() {
		const startPage = parseInt(document.getElementById('startPage').value);
		const displayPageNum = parseInt(document.getElementById('displayPageNum').value);
		const prevPage = startPage - displayPageNum;

		if (prevPage >= 1) {
			movePage(prevPage);
		}
	});

	// 다음 버튼 클릭
	document.querySelector('.next').addEventListener('click', function() {
		const endPage = parseInt(document.getElementById('endPage').value);
		const totalPage = parseInt(document.getElementById('totalPage').value);
		const nextPage = endPage + 1;

		if (nextPage <= totalPage) {
			movePage(nextPage);
		}
	});

	//정렬 조건 변경
	document.getElementById('sortBy').addEventListener('change', function() {
		movePage(1);
	});

	//페이지당 항목 수 변경
	document.getElementById('itemsPerPage').addEventListener('change', function() {
		movePage(1);
	});

	// 검색 버튼 클릭
	document.getElementById('search').addEventListener('click', function() {
		movePage(1);
	});

	//초기화 버튼 클릭
	document.getElementById('reset').addEventListener('click', function() {
		location.href = '/corp/applicant';
	});

	//엔터키 눌러도 검색
	document.getElementById('keyword').addEventListener('keydown', function(e) {
		if (e.key == 'Enter') {
			e.preventDefault();
			movePage(1);
		}
	});

	//이력서 클릭시 지원자 상세보기로 이동
	document.querySelectorAll('.col-resume.active').forEach(e => {
		e.addEventListener('click', function() {
			const resumeSeq = this.getAttribute('data-resume');
			const jobPostingSeq = this.getAttribute('data-posting');
			const jobApplicationSeq = this.getAttribute('data-seq');
			location.href = `/corp/applicant/resume/${resumeSeq}/${jobPostingSeq}/${jobApplicationSeq}`;
		});
	});

	//엑셀 출력 버튼 클릭
	document.getElementById('excelExport').addEventListener('click', function() {
		const params = new URLSearchParams();

		const postingStatus = document.querySelector('select[name="postingStatus"]').value;
		const postingTitle = document.querySelector('select[name="postingTitle"]').value;
		const applicationStatus = document.querySelector('select[name="applicationStatus"]').value;
		const passStage = document.querySelector('select[name="passStage"]').value;
		const keyword = document.querySelector('input[name="keyword"]').value.trim();
		const sortBy = document.getElementById('sortBy').value;

		params.append('postingStatus', postingStatus);
		params.append('postingTitle', postingTitle);
		params.append('applicationStatus', applicationStatus);
		params.append('passStage', passStage);
		params.append('keyword', keyword);
		params.append('sortBy', sortBy);

		const queryString = params.toString();
		location.href = '/corp/applicant/excel?' + queryString;
	});

	//pdf 출력 버튼 클릭
	document.getElementById('pdfExport').addEventListener('click', function() {
		const params = new URLSearchParams();

		const postingStatus = document.querySelector('select[name="postingStatus"]').value;
		const postingTitle = document.querySelector('select[name="postingTitle"]').value;
		const applicationStatus = document.querySelector('select[name="applicationStatus"]').value;
		const passStage = document.querySelector('select[name="passStage"]').value;
		const keyword = document.querySelector('input[name="keyword"]').value.trim();
		const sortBy = document.getElementById('sortBy').value;

		params.append('postingStatus', postingStatus);
		params.append('postingTitle', postingTitle);
		params.append('applicationStatus', applicationStatus);
		params.append('passStage', passStage);
		params.append('keyword', keyword);
		params.append('sortBy', sortBy);

		const queryString = params.toString();
		location.href = '/corp/applicant/download/all?' + queryString;
	});

});

//검색조건, 페이징, 정렬조건 선택 후 페이지 이동
/*function movePage(currentPage) {
	const params = new URLSearchParams({
		page: currentPage,
		postingStatus: document.querySelector('select[name="postingStatus"]').value,
		postingTitle: document.querySelector('select[name="postingTitle"]').value,
		applicationStatus: document.querySelector('select[name="applicationStatus"]').value,
		passStage: document.querySelector('select[name="passStage"]').value,
		keyword: document.querySelector('input[name="keyword"]').value,
		sortBy : document.getElementById('sortBy').value,
		size : document.getElementById('itemsPerPage').value
	});

	location.href = '/corp/applicant?' + params.toString()
} */

//검색조건, 페이징, 정렬조건 선택 후 페이지 이동
function movePage(currentPage) {
	const params = new URLSearchParams();

	// 페이지는 1이 아닌 경우에만 추가
	if (currentPage != 1) {
		params.append('page', currentPage);
	}

	// 각 값들을 기본값과 비교해서 다른 경우에만 추가
	const postingStatus = document.querySelector('select[name="postingStatus"]').value;
	const postingTitle = document.querySelector('select[name="postingTitle"]').value;
	const applicationStatus = document.querySelector('select[name="applicationStatus"]').value;
	const passStage = document.querySelector('select[name="passStage"]').value;
	const keyword = document.querySelector('input[name="keyword"]').value.trim();
	const sortBy = document.getElementById('sortBy').value;
	const itemsPerPage = document.getElementById('itemsPerPage').value;

	// 기본값이 아닌 경우에만 쿼리스트링에 추가
	// 공고 상태 (기본값: 빈 문자열 - "전체 공고")
	if (postingStatus && postingStatus !== '') {
		params.append('postingStatus', postingStatus);
	}

	// 공고 제목 (기본값: "0" 또는 빈 값)
	if (postingTitle && postingTitle !== '0' && postingTitle !== '') {
		params.append('postingTitle', postingTitle);
	}

	// 지원 상태 (기본값: "3" - "전체 지원상태")
	if (applicationStatus && applicationStatus !== '3') {
		params.append('applicationStatus', applicationStatus);
	}

	// 통과 단계 (기본값: "4" - "전체 합격 상태")
	if (passStage && passStage !== '4') {
		params.append('passStage', passStage);
	}

	// 검색어 (빈 문자열이 아닌 경우)
	if (keyword) {
		params.append('keyword', keyword);
	}

	// 정렬 조건 (기본값: "new" - "최신순")
	if (sortBy && sortBy !== 'new') {
		params.append('sortBy', sortBy);
	}

	// 페이지당 항목 수 (기본값: "10" - "10개")
	if (itemsPerPage && itemsPerPage !== '10') {
		params.append('size', itemsPerPage);
	}

	// 쿼리스트링이 있으면 ? 추가, 없으면 그냥 경로만
	const queryString = params.toString();
	location.href = '/corp/applicant' + (queryString ? '?' + queryString : '');
}

//전체, 진행중, 마감 공고 선택에 따른 공고 종류 변경
function updatePostingTitle(status, selectedValue) {

	const postingTitle = document.querySelector('select[name="postingTitle"]');

	fetch('/corp/applicant?status=' + status, {
		method: 'POST'
	})
		.then(response => {
			if (!response.ok) throw new Error('서버 오류 : ' + response.status)
			return response.json();
		})
		.then(data => {
			postingTitle.innerHTML = '';

			// 기본 옵션 추가
			const defaultOption = document.createElement('option');
			defaultOption.textContent = data.defaultOption;
			defaultOption.value = "0";

			// 선택된 값이 없거나 0
			if (!selectedValue || selectedValue === "0") {
				defaultOption.selected = true;
			}

			postingTitle.appendChild(defaultOption);

			// 받아온 공고 리스트 추가
			data.postings.forEach(posting => {
				const opt = document.createElement('option');
				opt.textContent = posting.postingTitle;
				opt.value = posting.jobPostingSeq;

				// 선택된 값과 일치하면 selected 설정
				if (selectedValue && posting.jobPostingSeq == selectedValue) {
					opt.selected = true;
				}

				postingTitle.appendChild(opt);
			});

		})
		.catch(error => {
			alert("요청 실패 : " + error.message)
		})
}