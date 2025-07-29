let currentPage = 1;
let isLastPage = false;
let loading = false;

document.addEventListener('DOMContentLoaded', function() {

	//지원한 이력서 클릭시
	document.querySelectorAll('.view-resume').forEach(function(element) {
		element.addEventListener('click', function(e) {
			const resumeSeq = e.target.getAttribute("data-resumeSeq")
			location.href = "/user/resume/resume_form/" + resumeSeq;
		});
	});

	//각 필터 버튼 클릭시
	document.querySelectorAll('.stats-item').forEach(item => {
		item.addEventListener('click', function() {
			const type = item.dataset.filter;
			movePage(type, "");

		});
	});

	//체크박스 클릭시
	const checkbox = document.getElementById('exclude-canceled');

	checkbox.addEventListener('change', function() {
		if (checkbox.checked) {
			movePage("", "false")
		} else {
			movePage("", "true")
		}
	});

	//날짜 옵션 변경시
	document.getElementById('period').addEventListener('change', function() {
		movePage("", "")
	});

	document.addEventListener('click', async function(e) {
		if (!e.target.classList.contains('cancel-applicant')) return;

		const jobApplicationSeq = e.target.getAttribute("data-applicantseq");

		if (e.target.innerText === '지원취소') {

			if (!confirm("정말로 지원을 취소하시겠습니까?")) return;

			try {
				const response = await fetch('/mypage/application/' + jobApplicationSeq, {
					method: "PUT"
				});
				const result = await response.text();

				if (response.ok && result === "success") {
					alert("지원 취소 완료");
					const applicationItem = e.target.closest(".application-item");
					applicationItem.classList.add('read-mail');

					e.target.innerText = "삭제";

					const badge = applicationItem.querySelector(".badge");
					badge.classList.add('status-canceled');
					badge.textContent = "지원취소";
				} else {
					alert("지원 취소 실패");
				}
			} catch (error) {
				console.error("에러 발생:", error);
			}
			return;
		}

		if (e.target.innerText === '삭제') {

			if (!confirm("정말로 지원내역을 삭제하시겠습니까?")) return;

			try {
				const response = await fetch('/mypage/application/' + jobApplicationSeq, {
					method: 'DELETE'
				});

				if (response.ok) {
					alert("삭제 완료");
					e.target.closest(".application-item").remove();
				} else {
					alert("서버 응답 오류");
				}
			} catch (error) {
				console.error("에러 발생:", error);
				alert("에러 발생");
			}
		}
	});


	//스크롤 끝까지 내렸갔을 때
	window.addEventListener('scroll', async function() {
		const scrollTop = window.scrollY;
		const windowHeight = window.innerHeight;
		const docHeight = document.documentElement.scrollHeight;

		if (scrollTop + windowHeight >= docHeight - 10 && !isLastPage && !loading) {
			loading = true;
			await loadMoreDate();
		};
	});


});

//페이지 이동
function movePage(type, includeCanceled) {
	const params = new URLSearchParams(location.search);
	params.set("period", document.getElementById('period').value);

	if (type !== undefined && type !== "") {
		params.set("type", type);
	}

	if (includeCanceled != undefined && includeCanceled != "") {
		params.set("includeCanceled", includeCanceled);
	}

	location.href = "/user/mypage/apply_list?" + params.toString();
}

async function loadMoreDate() {
	const params = new URLSearchParams(window.location.search);

	const period = params.get("period");
	const type = params.get("type");
	const includeCanceled = params.get("includeCanceled");
	currentPage += 1;

	const query = {
		period: period,
		type: type,
		includeCanceled: includeCanceled,
		currentPage: currentPage
	};

	const queryString = new URLSearchParams(query);

	try {
		const response = await fetch('/mypage/apply?' + queryString.toString());

		if (!response.ok) throw new Error("네트워크 에러");

		const data = await response.json();
		if (data.applicants.length === 0) {
			isLastPage = true;
			return;
		}

		const listContainer = document.querySelector('.application-list');

		data.applicants.forEach(apply => {
			const item = document.createElement("div");
			item.className = "application-item";
			if (apply.applicationStatus == 2) item.classList.add("read-mail");

			item.innerHTML = `
				<div class="application-info">
					<div class="company-name">${apply.corpNm}</div>
					<div class="job-title">${apply.postingTitle}</div>
					<div class="application-meta">
						<span>${apply.dDay}</span> |
						<span class="view-resume" data-resumeseq="${apply.resumeSeq}">지원이력서</span> |
						<span>지원일시 : ${apply.applicationDate}</span> |
						<span class="cancel-applicant" data-applicantseq="${apply.jobApplicationSeq}">
							${apply.applicationStatus == 2 ? '삭제' : '지원취소'}
						</span>
					</div>
				</div>
				<div class="application-actions">
					<span class="badge ${apply.applicationStatus == 2 ? 'status-canceled' : ''}">
						${apply.applicationStatus == 2 ? '지원취소' : ''}
					</span>
				</div>
			`;

			listContainer.appendChild(item);
		});

	} catch (error) {
		console.error("불러오기 실패:",error);
	} finally{
		loading = false;
	}
}

